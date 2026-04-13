import { Injectable, inject } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatRequest {
  query: string;
  projectName: string;
}

export interface ChatResponse {
  answer: string;
}
export interface ImportRequest {
  projectName: string;
  source: string;
  type: 'local' | 'git';
}
@Injectable({
  providedIn: 'root',
})
export class Chat {
  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/v1/chat';

  askTrace(request: ChatRequest): Observable<ChatResponse> {
    debugger;
    return this.http.post<ChatResponse>(this.apiUrl, request);
  }
  getProjects(): Observable<string[]> {
    return this.http.get<string[]>('http://localhost:8080/api/v1/projects');
  }

  importProject(req: ImportRequest): Observable<string> {
    if (req.type === 'local') {
      debugger;
      return this.http.post(
        `http://localhost:8080/api/v1/ingest`,
        {
          projectName: req.projectName,
          sourceType: 'LOCAL_DIRECTORY',
          sourcePath: req.source,
        },
        { responseType: 'text' },
      );
    } else {
      return this.http.post(
        `http://localhost:8080/api/v1/ingest`,
        {
          projectName: req.projectName,
          sourceType: 'GIT',
          sourcePath: req.source,
        },
        { responseType: 'text' },
      );
    }
  }
}
