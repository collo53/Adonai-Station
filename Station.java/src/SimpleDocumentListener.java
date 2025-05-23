// Put this in its own file or as a static inner class
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SimpleDocumentListener implements DocumentListener {
    private final Runnable onChange;

    public SimpleDocumentListener(Runnable onChange) {
        this.onChange = onChange;
    }

    @Override public void insertUpdate(DocumentEvent e) { onChange.run(); }

    @Override public void removeUpdate(DocumentEvent e) { onChange.run(); }

    @Override public void changedUpdate(DocumentEvent e) { onChange.run(); }
}
