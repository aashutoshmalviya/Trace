import {
  Component,
  inject,
  signal,
  ViewChild,
  ElementRef,
  AfterViewChecked,
  OnInit,
} from '@angular/core';
// ... other imports
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';
import { Chat } from './services/chat';
import { ImportModalComponent } from './components/import-modal/import-modal';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, MarkdownModule, ImportModalComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
})
export class App implements AfterViewChecked, OnInit {
  private chatService = inject(Chat);

  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  isModalOpen = signal<boolean>(false);
  currentProject = signal<string>('');
  userQuery = signal<string>('');
  messages = signal<Message[]>([]);
  isLoading = signal<boolean>(false);

  projects = signal<string[]>([]);
  ngOnInit() {
    this.loadProjects();
  }

  loadProjects() {
    this.chatService.getProjects().subscribe({
      next: (data) => {
        this.projects.set(data);
        if (data.length > 0) {
          const firstProject = data[0];
          this.currentProject.set(firstProject);
          this.messages.set([{ 
            text: `Connected to database. Currently analyzing: ${firstProject}. What would you like to know?`, 
            sender: 'trace', 
            timestamp: new Date() 
          }]);
        } else {
          this.messages.set([{ 
            text: `Welcome to Trace.AI. Your database is empty. Please click the '+' button to import a project.`, 
            sender: 'trace', 
            timestamp: new Date() 
          }]);
        }
      },
      error: (err) => {
        console.error('Failed to load projects:', err);
        this.messages.set([{ 
          text: `⚠️ Could not connect to the database to fetch projects. Is Postgres/Spring Boot running?`, 
          sender: 'trace', 
          timestamp: new Date() 
        }]);
      }
    });
  }
  handleSuccessfulImport(newProject: string) {
    if (!this.projects().includes(newProject)) {
      this.projects.update((prev) => [...prev, newProject]);
    }
    this.selectProject(newProject);

    this.messages.set([
      {
        text: `✅ Successfully imported ${newProject}. What would you like to know about it?`,
        sender: 'trace',
        timestamp: new Date(),
      },
    ]);

    this.isModalOpen.set(false);
  }
  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop =
        this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) {}
  }

  selectProject(project: string) {
    this.currentProject.set(project);
    // Reset chat when switching projects
    this.messages.set([
      {
        text: `Switched to ${project}. How can I help?`,
        sender: 'trace',
        timestamp: new Date(),
      },
    ]);
  }

  sendMessage() {
    const query = this.userQuery().trim();
    if (!query) return;

    // 1. Add user message 
    this.messages.update((msgs) => [
      ...msgs,
      {
        text: query,
        sender: 'user',
        timestamp: new Date(),
      },
    ]);

    // 2. Clear input and show loading 
    this.userQuery.set('');
    this.isLoading.set(true);

    const activeProject = this.currentProject();
    if (!activeProject) return;

    // 3. Send to Backend
    this.chatService
      .askTrace({
        query: query,
        projectName: activeProject,
      })
      .subscribe({
        next: (res) => {
          this.messages.update((msgs) => [
            ...msgs,
            {
              text: res.answer,
              sender: 'trace',
              timestamp: new Date(),
            },
          ]);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error(err);
          this.messages.update((msgs) => [
            ...msgs,
            {
              text: '⚠️ Connection failed. Please check if the backend is running.',
              sender: 'trace',
              timestamp: new Date(),
            },
          ]);
          this.isLoading.set(false);
        },
      });
  }

  // Handle Enter key to send
  onKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
  // --- Insert Methods ---
  openModal() {
    console.log('Add button clicked! isModalOpen set to true.');

    this.isModalOpen.set(true);
  }

  closeModal() {
    this.isModalOpen.set(false);
  }
}

interface Message {
  text: string;
  sender: 'user' | 'trace';
  timestamp: Date;
}
