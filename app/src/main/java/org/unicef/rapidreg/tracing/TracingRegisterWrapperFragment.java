package org.unicef.rapidreg.tracing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class TracingRegisterWrapperFragment extends RecordRegisterWrapperFragment {
    public static final String TAG = TracingRegisterWrapperFragment.class.getSimpleName();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveTracing(SaveTracingEvent event) {
        if (!validateRequiredField()) {
            Toast.makeText(getActivity(), R.string.required_field_is_not_filled, Toast.LENGTH_LONG).show();
            return;
        }

        clearProfileItems();

        ArrayList<String> photoPaths = (ArrayList<String>) recordPhotoAdapter.getAllItems();
        ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                this.itemValues.getValues()), JsonObject.class));

        try {
            saveTracing(itemValues, photoPaths);
            Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
            Bundle args = new Bundle();
            args.putSerializable(RecordService.ITEM_VALUES, ItemValuesMap.fromItemValuesJsonObject(itemValues));
            args.putStringArrayList(RecordService.RECORD_PHOTOS, photoPaths);
            ((RecordActivity) getActivity()).turnToFeature(TracingFeature.DETAILS_FULL, args, null);
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, itemValues);
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) recordPhotoAdapter.getAllItems());
        ((TracingActivity) getActivity()).turnToFeature(TracingFeature.EDIT_FULL, args, null);
    }

    @Override
    protected void initItemValues() {
        if (getArguments() != null) {
            itemValues = (ItemValuesMap) getArguments().getSerializable(ITEM_VALUES);
            recordPhotoAdapter = new TracingPhotoAdapter(getContext(),
                    getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
        }
    }

    @Override
    protected void initFormData() {
        form = TracingFormService.getInstance().getCurrentForm();
        sections = form.getSections();
    }

    @NonNull
    protected FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (Section section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            Bundle args = new Bundle();
            args.putSerializable(RecordService.ITEM_VALUES, itemValues);
            args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) recordPhotoAdapter.getAllItems());
            pages.add(FragmentPagerItem.of(values[0], TracingRegisterFragment.class, args));
        }
        return pages;
    }

    private boolean validateRequiredField() {
        TracingFormRoot tracingForm = TracingFormService.getInstance().getCurrentForm();
        return RecordService.validateRequiredFields(tracingForm, itemValues);
    }

    private Tracing saveTracing(ItemValues itemValues, List<String> photoPaths) throws IOException {
        return TracingService.getInstance().saveOrUpdate(itemValues, photoPaths);
    }
}
