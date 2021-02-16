package bms.engine.engine.multipleExceptions;

public class XmlMultipleExceptions extends Exception{
    private  StringBuilder EXCEPTION_MESSAGE;

    public XmlMultipleExceptions() {
        this.EXCEPTION_MESSAGE =new StringBuilder("");
    }

    public void addException(String exceptionMessage){
        if (this.EXCEPTION_MESSAGE.length() == 0)
            this.EXCEPTION_MESSAGE.append("The following problems have occurred during the Import Process from the given XML file: \n");
        this.EXCEPTION_MESSAGE.append(exceptionMessage+ "\n");
    }

    public boolean hasExceptions(){
        return (this.EXCEPTION_MESSAGE.length() > 0);
    }

    @Override
    public String getMessage() {
        return EXCEPTION_MESSAGE.substring(0);
    }

}
