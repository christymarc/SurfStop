package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.surfstop.R;


public class PopupDialogFragment extends DialogFragment {

    private String popupText;
    private static final String POPUP_TEXT_KEY = "popup";

    TextView tvPopupText;
    Button okButton;

    public PopupDialogFragment() { }

    public static PopupDialogFragment newInstance(String popupText) {
        PopupDialogFragment fragment = new PopupDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(POPUP_TEXT_KEY, popupText);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.popupText = getArguments().getString(POPUP_TEXT_KEY);
        return inflater.inflate(R.layout.fragment_popup, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvPopupText = view.findViewById(R.id.tvPopupText);
        okButton = view.findViewById(R.id.okButton);

        tvPopupText.setText(popupText);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
