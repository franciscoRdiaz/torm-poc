import { StompService } from 'ng2-stomp-service';
import {Injectable} from "@angular/core";


@Injectable()
export class TestManagerService {

  private wsConf = {
    host: 'test.com',
    debug: true
  }
  private subscription: any;

  constructor(stomp: StompService) {

    /**
     * Stomp configuration.
     * @param {object} config: a configuration object.
     *                 {host:string} websocket endpoint
     *                 {headers?:Object} headers (optional)
     *                 {heartbeatIn?: number} heartbeats out (optional)
     *                 {heartbeatOut?: number} heartbeat in (optional)
     *                 {debug?:boolean} debuging (optional)
     *                 {recTimeout?:number} reconnection time (ms) (optional)
     */
    stomp.configure(this.wsConf);

    /**
     * Start connection
     * @return {Promise} if resolved
     */
    stomp.startConnect().then(() => {
      console.log('connected');

      /**
       * Subscribe.
       * @param {string} destination: subscibe destination.
       * @param {Function} callback(message,headers): called after server response.
       * @param {object} headers: optional headers.
       */
      this.subscription = stomp.subscribe('/destination', this.response);

      /**
       * Send message.
       * @param {string} destination: send destination.
       * @param {object} body: a object that sends.
       * @param {object} headers: optional headers.
       */
      stomp.send('destionation', {"data": "data"});

      /**
       * Unsubscribe subscription.
       */
      this.subscription.unsubscribe();

      /**
       * Disconnect
       * @return {Promise} if resolved
       */
      stomp.disconnect().then(() => {
        console.log('Connection closed')
      });

    });
  }

  // Response
  public response = (data) => {
    console.log(data);
  }
  /**
   * Created by frdiaz on 17/04/2017.
   */
}
