import { Component } from '@angular/core';
import { HeaderComponent } from './pages/templates/header/header.component';
import { FooterComponent } from './pages/templates/footer/footer.component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderComponent, FooterComponent, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {}
