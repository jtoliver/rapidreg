package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.CasePhotoService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class PhotoUploadMiniFormViewHolder extends BaseViewHolder<CaseField> {
    public static final String TAG = PhotoUploadMiniFormViewHolder.class.getSimpleName();

    @BindView(R.id.case_photo_view_slider)
    ViewPager viewPager;

    @BindView(R.id.indicator)
    CircleIndicator indicator;

    private Context context;

    public PhotoUploadMiniFormViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
    }

    @Override
    public void setValue(CaseField field) {
        viewPager.setAdapter(new CasePhotoViewPagerAdapter());
        indicator.setViewPager(viewPager);
    }

    @Override
    public void setOnClickListener(CaseField field) {

    }

    @Override
    protected String getResult() {
        return null;
    }

    @Override
    public void setFieldEditable(boolean editable) {
    }

    public class CasePhotoViewPagerAdapter extends PagerAdapter {
        private List<CasePhoto> flowQueryList;

        public CasePhotoViewPagerAdapter() {
            flowQueryList = CasePhotoService.getInstance().getAllCasesPhotoFlowQueryList(CasePhotoService.getInstance().getCaseId());
        }

        @Override
        public int getCount() {
            return flowQueryList.isEmpty() ? 1 : flowQueryList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.case_photo_view_item,
                    container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.case_photo_item);

            if (flowQueryList.isEmpty()) {
                Glide.with(context).load(R.drawable.photo_placeholder).centerCrop().into(imageView);
            } else {
                Glide.with(context).load(flowQueryList.get(position).getPhoto().getBlob())
                        .centerCrop().into(imageView);
            }
            container.addView(itemView);
            return itemView;
        }
    }
}
