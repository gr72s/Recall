import React from "react";
import { State, createState } from "./../state";
import {
  Dialog,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import ItemList from "./ItemList";

class BaseLayout extends React.Component<any, State> {
  state = createState(this);

  render(): React.ReactNode {
    return (
      <>
        <Dialog
          open={this.state.pleaseWaitVisible}
          disableEscapeKeyDown={true}
          transitionDuration={0}
          onClose={(inEvent, inReason) => {
            if (inReason !== "backdropClick") {
              this.state.showHidePleaseWait(false);
            }
          }}
        >
          <DialogTitle style={{ textAlign: "center" }}>Please Wait</DialogTitle>
          <DialogContent>
            <DialogContentText>...Contacting server...</DialogContentText>
          </DialogContent>
        </Dialog>
        <ItemList />
      </>
    );
  }
}

export default BaseLayout;
