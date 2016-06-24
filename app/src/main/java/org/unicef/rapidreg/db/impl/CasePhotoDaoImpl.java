package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.CasePhoto_Table;

import java.util.List;

public class CasePhotoDaoImpl implements CasePhotoDao {
    @Override
    public List<CasePhoto> getAllCasesPhoto(long caseId) {
        return SQLite.select()
                .from(CasePhoto.class)
                .where(CasePhoto_Table.case_id.eq(caseId))
                .queryList();
    }

    public void deleteCasePhotosByCaseId(long caseId) {
        SQLite.delete().from(CasePhoto.class).where(CasePhoto_Table.case_id.eq(caseId)).execute();
    }
}