package android.print;

import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

public class PdfCallbackHelper {

    public interface LayoutCallback {
        void onLayoutFinished(PrintDocumentInfo info, boolean changed);
        void onLayoutFailed(CharSequence error);
    }

    public interface WriteCallback {
        void onWriteFinished(PageRange[] pages);
        void onWriteFailed(CharSequence error);
    }

    public static void printLayout(
            PrintDocumentAdapter adapter,
            PrintAttributes prevAttributes,
            PrintAttributes newAttributes,
            final LayoutCallback callback
    ) {
        PrintDocumentAdapter.LayoutResultCallback printLayoutCallback = new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                if (callback != null) {
                    callback.onLayoutFinished(info, changed);
                }
            }

            @Override
            public void onLayoutFailed(CharSequence error) {
                if (callback != null) {
                    callback.onLayoutFailed(error);
                }
            }
        };

        adapter.onLayout(prevAttributes, newAttributes, new CancellationSignal(), printLayoutCallback, null);
    }

    public static void printWrite(
            PrintDocumentAdapter adapter,
            PageRange[] pages,
            ParcelFileDescriptor destination,
            final WriteCallback callback
    ) {
        PrintDocumentAdapter.WriteResultCallback printWriteCallback = new PrintDocumentAdapter.WriteResultCallback() {
            @Override
            public void onWriteFinished(PageRange[] pages) {
                if (callback != null) {
                    callback.onWriteFinished(pages);
                }
            }

            @Override
            public void onWriteFailed(CharSequence error) {
                if (callback != null) {
                    callback.onWriteFailed(error);
                }
            }
        };

        adapter.onWrite(pages, destination, new CancellationSignal(), printWriteCallback);
    }
}
