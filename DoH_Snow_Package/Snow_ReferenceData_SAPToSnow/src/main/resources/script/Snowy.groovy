import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;

def Message processData(Message message)
{   
    def body = message.getBody(java.lang.String) as String;
    def messageLog = messageLogFactory.getMessageLog(message);
    if(messageLog != null)
    {
        messageLog.setStringProperty("log1","Printing Payload As Attachment")
        messageLog.addAttachmentAsString("log1",body,"text/plain");
    }
    return message;
}

def Message setTargetUri(Message message)
{   
    def sEntity = message.getProperty("sEntity") as String;
    switch (sEntity){
        case "OrgUnit":
        message.setProperty("SnowApiServiceUri", message.getProperty("SnowApiOrgChartUri"));
        break;
        case "CompanyCode":
        message.setProperty("SnowApiServiceUri", message.getProperty("SnowApiCompanyCodeUri"));            
        break;
    }
    return message;
}

def Message logPayload(Message message)
{   
    def body = message.getBody(java.lang.String) as String;
    def messageLog = messageLogFactory.getMessageLog(message);
    if(messageLog != null)
    {
        messageLog.setStringProperty("log1","Printing Payload As Attachment")
        messageLog.addAttachmentAsString("log1",body,"text/plain");
    }
    return message;
}