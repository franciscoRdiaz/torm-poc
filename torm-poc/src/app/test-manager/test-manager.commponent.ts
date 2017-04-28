import {AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild} from "@angular/core";
import {TestManagerService} from "./test-manager.service";
import {TestInfo} from "./test-info";
import {StompWSManager} from "./stomp-ws-manager.service";
import {LogTrace} from "./log-trace";
import {Image} from "./image";


@Component({
  selector: 'qs-test-manager',
  templateUrl: './test-manager.commponent.html',
  styleUrls: ['./test-manager.component.scss']
})

export class TestManagerComponent implements  OnInit, OnDestroy, AfterViewChecked{
  @ViewChild('scrollMe') private myScrollContainer: ElementRef;

  testInfo: TestInfo = undefined;
  traces: LogTrace[] = [];
  images: Image[] = [];

  constructor(private testManagerService: TestManagerService, private stompWSManager: StompWSManager) {
    /* TODO: in the future, fill it with the database content */
    this.images.push({name:"edujgurjc/torm-test-01"});
    this.images.push({name:"edujgurjc/torm-test-02"});
  }

  ngOnInit(){
    this.stompWSManager.configWSConnection('/logs');
    this.stompWSManager.startWsConnection();
    this.traces = this.stompWSManager.traces;
  }

  ngOnDestroy(){
    this.stompWSManager.disconnectWSConnection();
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch(err) { }
  }


  createAndRunTest(){
    this.stompWSManager.traces.splice(0, this.stompWSManager.traces.length);
    this.testInfo = new TestInfo();
    this.testInfo.imageName = this.images[1].name;
    this.testManagerService.createAndRunTest(this.testInfo)
      .subscribe(
        testInfo => {
          this.testInfo.id = testInfo.id;
          console.log('idContainer:'+this.testInfo.id)
        },
        error => console.error("Error:" + error)
      );
  }

  sendMessage(){
    this.stompWSManager.sendWSMessage();
  }

}
