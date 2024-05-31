import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import BaseLayout from "./components/BaseLayout";
import { getState } from "./state";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);
root.render(<BaseLayout />);

const intervalFunction = (): void => {
  if (getState() === null) {
    setTimeout(intervalFunction, 100);
  } else {
    startupFunction();
  }
};

intervalFunction();

const startupFunction = (): void => {
  getState().showHidePleaseWait(true);
  setTimeout(() => {
    getState().showHidePleaseWait(false);
  }, 4000);
};
