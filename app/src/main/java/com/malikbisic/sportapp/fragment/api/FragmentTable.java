package com.malikbisic.sportapp.fragment.api;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malikbisic.sportapp.activity.StopAppServices;

/**
 * Created by Nane on 31.12.2017.
 */

public class FragmentTable extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent closeAPP = new Intent(getContext(), StopAppServices.class);
        getActivity().startService(closeAPP);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
