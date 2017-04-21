import { Injectable } from '@angular/core';
import {Http, Response} from '@angular/http';
import {TestInfo} from './test-info';
import 'rxjs/Rx';

@Injectable()
export class TestManagerService {

  constructor (private http: Http) {}

  createAndRunTest(testInfo: TestInfo) {
    let url = 'http://localhost:8080/containers/';
    return this.http.post(url, testInfo)
      .map(response => this.createTestInfo(response.json()))
  }

  getTestResults(){
    let url = 'http://localhost:8080/containers/testInfo';
    return this.http.get(url)
      .map(response => this.createTestInfo(response.json()))
  }

  createTest( testInfo: any ) {
    return 1;
  }

  runTest(testInfo: any) {
    return 1;
  }

  createTestInfo(testInfo: any) {
    return testInfo;
  }
}
