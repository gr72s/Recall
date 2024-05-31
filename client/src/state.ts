import React from "react";

let stateSingleton: any = null;

type State = {
  pleaseWaitVisible: boolean;
  showHidePleaseWait: (visible: boolean) => void;
};

const createState = (component: React.Component): State => {
  if (stateSingleton === null) {
    stateSingleton = {
      pleaseWaitVisible: false,
      showHidePleaseWait: function(visible: boolean): void {
        this.setState(() => ({ pleaseWaitVisible: visible }));
      }.bind(component),
    };
  }
  return stateSingleton;
};

const getState = (): State => {
  return stateSingleton;
};

export type { State };
export { createState, getState };
