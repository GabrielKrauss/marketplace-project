import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FileDownloadService {

  constructor(private http: HttpClient) {}

  downloadRawTextAsTxt(url: string, filename: string) {
    const proxyUrl = `http://localhost:8080/api/proxy/download?url=${encodeURIComponent(url)}`;
    this.http.get(proxyUrl, { responseType: 'text' }).subscribe(text => {
      // Cria um Blob com o conteúdo em formato de texto
      
      const blob = new Blob([text], { type: 'text/plain' });
      const objectUrl = URL.createObjectURL(blob);
      
      // Cria um link temporário e força o download
      const a = document.createElement('a');
      a.href = objectUrl;
      a.download = filename; // Ex: 'arquivo.txt'
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(objectUrl);
    }, error => {
      console.error('Erro ao baixar o arquivo:', error);
    });
  }
}
