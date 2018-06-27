package nl.avans.smtpstatemachine;

import nl.avans.SmtpContext;

public class ReceivingDataState implements SmtpStateInf {
    SmtpContext context;
    boolean crReceived;
    boolean dotReceived;

    public ReceivingDataState(SmtpContext context) {
        this.context = context;
    }

    @Override
    public void Handle(String data) {

        //Check for a carriage return
        if(data.equals("")) {
            crReceived = true;
            return;
        }

        //After the carriage return is detected and a . is detected (End of email body) set boolean and set boolean to true for dotReceived
        if(data.equals(".") && crReceived) {
            dotReceived = true;
        }

        //End of email body
        if(crReceived && dotReceived) {
            context.SendData("250 Ok: Email message received");
            context.SetNewState(new WaitForMailFromState(context));
            //Reset booleans
            crReceived = false;
            dotReceived = false;
            return;
        }

        //If line is not empty attach to email body
        if(!data.isEmpty()) {
            context.AddTextToBody(data);
            //Reset the boolean for checking if there was a carriage return.
            crReceived = false;
            return;
        }

        // "\r\n.\r\n" should return to the WaitForMailFromState
    }
}
