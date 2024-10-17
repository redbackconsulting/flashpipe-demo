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

def Message grabErrorResponse(Message message) {
                
    // get a map of properties
    def map = message.getProperties();
                
    // get an exception java class instance
    def ex = map.get("CamelExceptionCaught");
    if (ex!=null) {
                                
        // an http adapter throws an instance of org.apache.camel.component.ahc.AhcOperationFailedException
        if (ex.getClass().getCanonicalName().equals("org.apache.camel.component.ahc.AhcOperationFailedException")) {
                                        
            // save the http error response as a message attachment 
                def messageLog = messageLogFactory.getMessageLog(message);
                messageLog.addAttachmentAsString("http.Exception.ResponseBody", ex.getResponseBody(), "text/plain");

            // copy the http error response to an exchange property
                message.setProperty("http.Exception.ResponseBody",ex.getResponseBody());

            // copy the http error response to the message body
                message.setBody(ex.getResponseBody());

            // copy the value of http error code (i.e. 500) to a property
                message.setProperty("http.Exception.StatusCode",ex.getStatusCode());

            // copy the value of http error text (i.e. "Internal Server Error") to a property
                message.setProperty("http.Exception.StatusText",ex.getStatusText());
                                        
        }
    }
    return message;
}