package nybergtechnology.com.webuildapps.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;

import nybergtechnology.com.webuildapps.R;

/**
 * Created by Administrator on 1/25/2016.
 */
public class AccountFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    Context m_Context;
    public View m_myContainer;

    ImageView menuButton;

    public static AccountFragment newInstance(int sectionNumber) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_fragment, container, false);

        m_myContainer = rootView;
        m_Context = getActivity();

        return rootView;
    }

}
