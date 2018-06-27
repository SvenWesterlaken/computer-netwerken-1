package nl.avans.smtpstatemachine;

import nl.avans.SmtpContext;

public class WaitForRcptToOrDataState implements SmtpStateInf {
    SmtpContext context;

    public WaitForRcptToOrDataState(SmtpContext context) {
        this.context = context;
    }

    @Override
    public void Handle(String data) {
        if(data.toUpperCase().startsWith("RCPT TO: ")) {
            context.AddRecipient(data.substring(9));
            context.SendData("250 Ok");
            return;
        }
        if(data.toUpperCase().startsWith("DATA")) {
            context.SendData("354 End data with <CR><LF>.<CR><LF>");
            context.SetNewState(new ReceivingDataState(context));
            return;
        }
        if(data.toUpperCase().startsWith("QUIT")) {
            context.SendData("221 Bye");
            context.DisconnectSocket();
            return;
        }
        context.SendData("503 Error...");

        //Handle "MAIL FROM: <user@domain.nl>" Command & TRANSITION TO NEXT STATE
        //Handle "DATA" Command & TRANSITION TO NEXT STATE
        //Handle "QUIT" Command
        //Generate "503 Error on invalid input"
    }
}
