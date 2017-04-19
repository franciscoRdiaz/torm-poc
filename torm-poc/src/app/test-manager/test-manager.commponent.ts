import {Component, OnDestroy, OnInit} from "@angular/core";
import {TestManagerService} from "./test-manager.service";
import {TestInfo} from "./test-info";
import {StompWSManager} from "./stomp-ws-manager.service";


@Component({
  selector: 'qs-test-manager',
  templateUrl: './test-manager.commponent.html',
  styleUrls: ['./test-manager.component.scss'],
})

export class TestManagerComponent implements  OnInit, OnDestroy{

  testInfo: TestInfo = undefined;

  constructor(private testManagerService: TestManagerService, private stompWSManager: StompWSManager) {}

  ngOnInit(){
    this.stompWSManager.configWSConnection('/logs');
    this.stompWSManager.startWsConnection();
  }

  ngOnDestroy(){
    this.stompWSManager.disconnectWSConnection();
  }

  createAndRunTest(){
    this.testInfo = new TestInfo();
    this.testManagerService.createAndRunTest(this.testInfo)
      .subscribe(
        testInfo => {
          this.testInfo.id = testInfo.id;
          console.log('idContainer:'+this.testInfo.id
          )
        },
        error => console.error("Error:" + error)
      );
  }

  sendMessage(){
    this.stompWSManager.sendWSMessage();
  }

}
