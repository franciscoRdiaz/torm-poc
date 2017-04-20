/**
 * Created by frdiaz on 19/04/2017.
 */
import {Injectable} from "@angular/core";
import { StompService } from 'ng2-stomp-service';
import {LogTrace} from "./LogTrace";

@Injectable()
export class StompWSManager{

  private wsConf = {
    host: '/logs',
    debug: true
  }

  private subscription: any;

  traces: LogTrace[] = [];

  constructor(private stomp: StompService){}

  configWSConnection(host: string){
    this.wsConf.host = host;
    this.stomp.configure(this.wsConf);
  }

  startWsConnection(){
    /**
     * Start connection
     * @return {Promise} if resolved
     */
    this.stomp.startConnect().then(() => {
      console.log('connected');

      /**
       * Subscribe.
       * @param {string} destination: subscibe destination.
       * @param {Function} callback(message,headers): called after server response.
       * @param {object} headers: optional headers.
       */
      this.subscription = this.stomp.subscribe('/topic/logs', this.response);
    });
  }

  /**
   * Disconnect
   * @return {Promise} if resolved
   */
  disconnectWSConnection() {
    this.stomp.disconnect().then(() => {
      console.log('Connection closed')
    });
  }

  subscribeWSDestination(){
    /**
     * Subscribe.
     * @param {string} destination: subscibe destination.
     * @param {Function} callback(message,headers): called after server response.
     * @param {object} headers: optional headers.
     */
    this.subscription = this.stomp.subscribe('/topic/logs', this.response);
  }

  ususcribeWSDestination(){
    /**
     * Unsubscribe subscription.
     */
    this.subscription.unsubscribe();
  }

  sendWSMessage(){
    /**
     * Send message.
     * @param {string} destination: send destination.
     * @param {object} body: a object that sends.
     * @param {object} headers: optional headers.
     */
    this.stomp.send('/topic/logs', {"data": "data"});
  }

  // Response
  public response = (data) => {
    console.log(data);
    this.traces.push(data);
  }
}
