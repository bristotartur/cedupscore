import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, Output, QueryList, SimpleChanges, ViewChild, ViewChildren } from '@angular/core';

const buttonsRef = 'boldBtn, italicBtn, underlineBtn, orderedListBtn, unorderedListBtn';

@Component({
  selector: 'app-text-editor',
  standalone: true,
  imports: [],
  templateUrl: './text-editor.component.html',
  styleUrl: './text-editor.component.scss',
})
export class TextEditorComponent implements OnChanges, AfterViewInit {
  
  @ViewChildren(buttonsRef) buttons!: QueryList<ElementRef<HTMLButtonElement>>;
  @ViewChild('output') output!: ElementRef<HTMLDivElement>;

  @Input() content!: string;

  @Output('contentChange') contentChange = new EventEmitter<string>();

  ngOnChanges(changes: SimpleChanges): void {
    if (this.output && changes['content']) {
      this.output.nativeElement.innerHTML = this.content ?? '';
    }
  }

  ngAfterViewInit(): void {
    this.buttons.forEach(button => {
      button.nativeElement.addEventListener('click', () => {
        this.handleButtonOperation(button.nativeElement);
      });
    });
    this.output.nativeElement.addEventListener('keydown', this.onTabPressed.bind(this));
    this.output.nativeElement.addEventListener('input', this.onContentChange.bind(this));
    this.output.nativeElement.innerHTML = (this.content) ? this.content : '';
  }

  private handleButtonOperation(button: HTMLButtonElement): void {
    const command = button.dataset['command'];

    if (command) {
        document.execCommand(command);
        this.addSpacingToLists();
    }
  }

  private addSpacingToLists(): void {
    const outputDiv = this.output.nativeElement;
    const lists = outputDiv.querySelectorAll('ul, ol');

    lists.forEach(list => {
      (list as HTMLElement).style.margin = '.5em 0';
      (list as HTMLElement).style.paddingLeft = '2em';
    });
  }

  private onTabPressed(e: KeyboardEvent): void {
    if (e.key === 'Tab') {
      e.preventDefault();
        const tabSpaces = '    ';
        document.execCommand('insertText', false, tabSpaces);
    }
  }

  private onContentChange(): void {
    const content = this.output.nativeElement.innerHTML;
    console.log(content);
    this.contentChange.emit(content);
  }

}
