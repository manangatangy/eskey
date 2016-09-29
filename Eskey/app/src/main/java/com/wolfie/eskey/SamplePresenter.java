package com.wolfie.eskey;

import com.wolfie.eskey.SamplePresenter.SampleUi;
import com.wolfie.eskey.presenter.BasePresenter;
import com.wolfie.eskey.view.BaseUi;

public class SamplePresenter extends BasePresenter<SampleUi> {

    public SamplePresenter(SampleUi sampleUi) {
        super(sampleUi);
    }

    public interface SampleUi extends BaseUi {

    }

}
