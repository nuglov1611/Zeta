package core.document.exception;

/*
 * @author uglov
 */
public class LoadDocumentException extends Exception {
    public LoadDocumentException(String doc_name) {
        super("Error during loading core.document: " + doc_name);
    }

    public LoadDocumentException() {
        super("Error during loading core.document!");
    }

}
