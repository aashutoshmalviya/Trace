import { Component, inject, signal, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chat, ImportRequest } from '../../services/chat';

@Component({
  selector: 'app-import-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './import-modal.html',
  styleUrls: ['./import-modal.scss'],
})
export class ImportModalComponent {
  private chatService = inject(Chat);

  
  closeEvent = output<void>();
  importSuccess = output<string>(); 

  
  importType = signal<'local' | 'git'>('local');
  newProjectName = signal<string>('');
  newProjectSource = signal<string>('');
  isImporting = signal<boolean>(false);

  setImportType(type: 'local' | 'git') {
    this.importType.set(type);
    this.newProjectSource.set(''); 
  }

  submitImport() {
    if (!this.newProjectName() || !this.newProjectSource()) return;

    this.isImporting.set(true);

    const req: ImportRequest = {
      projectName: this.newProjectName(),
      source: this.newProjectSource(),
      type: this.importType(),
    };

    this.chatService.importProject(req).subscribe({
      next: () => {
        this.isImporting.set(false);
        this.importSuccess.emit(req.projectName);
        this.resetAndClose();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to import project.');
        this.isImporting.set(false);
      },
    });
  }

  resetAndClose() {
    this.newProjectName.set('');
    this.newProjectSource.set('');
    this.closeEvent.emit();
  }
}
