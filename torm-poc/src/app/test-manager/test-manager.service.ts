import { Injectable } from '@angular/core';
import {Http, Response} from '@angular/http';
import {TestInfo} from './test-info';
import {TestResult} from './test-result';
import 'rxjs/Rx';

@Injectable()
export class TestManagerService {

  constructor (private http: Http) {}

  createAndRunTest(testInfo: TestInfo) {
    //let url = 'http://localhost:8080/containers/';
    let url = 'http://localhost:8090/containers/external/api/';
      return this.http.post(url, testInfo)
      .map(response => this.createTestInfo(response.json()))
  }

  getTestResults(){
    console.log("Invoking api rest to get the test results");
    let url = 'http://localhost:8090/containers/testInfo';
    return this.http.get(url)
      .map(
        response => this.createTestInfo(response.json()),
        error => console.log(error)
      )
  }

  createTest( testInfo: any ) {
    return 1;
  }

  runTest(testInfo: any) {
    return 1;
  }

  createTestInfo(testInfo: any[]) {
    console.log("Test info retrives:" +testInfo[0].numberOfErrors);
    var testResult = new TestResult(testInfo[0].numberOfTests, testInfo[0].numberOfErrors, testInfo[0].numberOfFailures, testInfo[0].numberOfSkipped);
    return testResult;
  }
}
