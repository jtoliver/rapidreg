package org.unicef.rapidreg.base.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseListFragment;
import org.unicef.rapidreg.service.UserService;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView textViewLoginUserLabel;
    TextView textViewLogoutLabel;

    IntentSender intentSender = new IntentSender();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        View headerView = navigationView.getHeaderView(0);
        textViewLoginUserLabel = (TextView) headerView.findViewById(R.id.login_user_label);
        textViewLoginUserLabel.setText(getIntent().getStringExtra(IntentSender.KEY_LOGIN_USER));
        textViewLogoutLabel = (TextView) headerView.findViewById(R.id.logout_label);

        final BaseActivity baseActivity = this;
        textViewLogoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogout(baseActivity);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        drawer.openDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            getIntent().putExtra(CaseActivity.INTENT_KEY_CASE_MODE, CaseActivity.CaseMode.LIST);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_cases) {
            navCaseAction();
        } else if (id == R.id.nav_tracing) {

        }
        navigationView.getMenu().findItem(id).setChecked(true);
        return true;
    }

    public void navCaseAction() {
        if (isCaseInEdit()) {
            new AlertDialog.Builder(this)
                    .setTitle("Quit")
                    .setMessage("Are you sure to quit without saving?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            redirectToCaseListPage();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
        } else {
            redirectToCaseListPage();
        }
    }

    private void redirectToCaseListPage() {
        setTopMenuItemsInCaseListPage();
        navigationView.getMenu().findItem(R.id.nav_cases).setChecked(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, new CaseListFragment())
                .commit();
    }

    public void setTopMenuItemsInCaseDetailPage() {
        getIntent().putExtra(CaseActivity.INTENT_KEY_CASE_MODE, CaseActivity.CaseMode.DETAIL);
        setEditCaseVisible(true);
        setAddCaseVisible(false);
        setSaveCaseVisible(false);
    }

    public void setTopMenuItemsInCaseListPage() {
        getIntent().putExtra(CaseActivity.INTENT_KEY_CASE_MODE, CaseActivity.CaseMode.LIST);
        setEditCaseVisible(false);
        setAddCaseVisible(true);
        setSaveCaseVisible(false);
    }

    public void setTopMenuItemsInCaseAdditionPage() {
        getIntent().putExtra(CaseActivity.INTENT_KEY_CASE_MODE, CaseActivity.CaseMode.ADD);
        setEditCaseVisible(false);
        setAddCaseVisible(false);
        setSaveCaseVisible(true);
    }

    public void setTopMenuItemsInCaseEditPage() {
        getIntent().putExtra(CaseActivity.INTENT_KEY_CASE_MODE, CaseActivity.CaseMode.EDIT);
        setEditCaseVisible(false);
        setAddCaseVisible(false);
        setSaveCaseVisible(true);
    }

    public boolean isCaseInEdit() {
        return toolbar.getMenu().findItem(R.id.save_case).isVisible();
    }

    private void setAddCaseVisible(boolean visible) {
        toolbar.getMenu().findItem(R.id.add_case).setVisible(visible);
    }

    private void setSaveCaseVisible(boolean visible) {
        toolbar.getMenu().findItem(R.id.save_case).setVisible(visible);
    }

    private void setEditCaseVisible(boolean visible) {
        toolbar.getMenu().findItem(R.id.edit_case).setVisible(visible);
    }

    //TODO: Put logout into basePresenter in future
    private void attemptLogout(BaseActivity currentActivity) {
        if (getContext().getSyncTask() != null) {
            createAlertDialog(currentActivity);
        } else {
            logOut(currentActivity);
        }
    }

    private void logOut(BaseActivity currentActivity) {
        PrimeroApplication context = currentActivity.getContext();
        String message = context.getResources().getString(R.string.login_out_successful_text);
        UserService.getInstance().setCurrentUser(null);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        intentSender.showLoginActivity(currentActivity);
    }

    private void createAlertDialog(BaseActivity currentActivity) {
        //TODO: alert box
    }

    public PrimeroApplication getContext() {
        return (PrimeroApplication) getApplication();
    }
}
