package so.wih.android.jjewatch.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import so.wih.android.jjewatch.R;

/**
 * Created by Administrator on 2016/11/24.
 */

public class AddContactFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View addContactView = inflater.inflate(R.layout.add_contact_fragment, null);
        return addContactView;
    }
}
