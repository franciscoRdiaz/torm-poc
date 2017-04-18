/**
 * Created by frdiaz on 11/04/2017.
 */

export class TestInfo {

  private _id: string;


  get id(): string {
    return this._id;
  }

  set id(value: string) {
    this._id = value;
  }
}
