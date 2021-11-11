package com.pgi.convergencemeetings.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.AddressDao;
import com.pgi.convergencemeetings.models.ClientContactDetailsDao;
import com.pgi.convergencemeetings.models.ClientDetailsDao;
import com.pgi.convergencemeetings.models.ClientInfoResponseDao;
import com.pgi.convergencemeetings.models.ClientInfoResultDao;
import com.pgi.convergencemeetings.models.CompanyDetailsDao;
import com.pgi.convergencemeetings.models.DaoMaster;
import com.pgi.convergencemeetings.models.DaoSession;
import com.pgi.convergencemeetings.models.MeetingInfoResponseDao;
import com.pgi.convergencemeetings.models.MeetingRoomDao;
import com.pgi.convergencemeetings.models.MeetingRoomDetailDao;
import com.pgi.convergencemeetings.models.MeetingRoomGetResultDao;
import com.pgi.convergencemeetings.models.MeetingRoomOptionDao;
import com.pgi.convergencemeetings.models.MeetingRoomUrlsDao;
import com.pgi.convergencemeetings.models.PhoneInformationDao;
import com.pgi.convergencemeetings.models.ReservationOptionsDao;
import com.pgi.convergencemeetings.models.UUIDDao;
import com.pgi.convergencemeetings.models.elkmodels.LogsModelDao;
import com.pgi.convergencemeetings.models.getPhoneNumberModel.PhoneDao;
import com.pgi.logging.Logger;
import com.pgi.logging.enums.LogEvent;
import com.pgi.logging.enums.LogEventValue;
import com.pgi.network.models.SearchResult;
import com.pgi.network.models.SearchResultDao;

import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.database.Database;

import java.util.List;

public class  ApplicationDao {

/**
 * Created by nnennaiheke on 8/6/17.
 */
    private static final String TAG = ApplicationDao.class.getSimpleName();
    private final Logger mLogger = ConvergenceApplication.mLogger;


    public static @NotNull
    ApplicationDao get(@NotNull Context context) {
        if(null == instance) {
            instance = new ApplicationDao(context, DATABASE_NAME, null);
        }
        return instance;
    }
    // for unit testing only
    public static void setInstance(ApplicationDao applicationDao) {
        instance = applicationDao;
    }

    public static final String WILDCARD = "%";
    public static final String DEFAULT_COLLATE_STRING = "COLLATE NOCASE ASC";

    public class DatabaseOpenHelper extends DaoMaster.OpenHelper {

        public DatabaseOpenHelper(@NotNull Context context, @NotNull String dbName, @Nullable SQLiteDatabase.CursorFactory cursorFactory) {
            super(context, dbName, cursorFactory);
        }

        @Override
        public void onCreate(Database db) {
            super.onCreate(db);
            DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, true);
            com.pgi.network.models.DaoMaster.dropAllTables(db, true);
            com.pgi.network.models.DaoMaster.createAllTables(db, true);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
                DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, true);
                com.pgi.network.models.DaoMaster.dropAllTables(db, true);
            com.pgi.network.models.DaoMaster.createAllTables(db, true);

        }
    }

    private ApplicationDao(@NotNull Context context, @NotNull String dbName, @Nullable SQLiteDatabase.CursorFactory cursorFactory) {
        if (context == null) {
            this.context = ConvergenceApplication.appContext;
        } else {
            this.context = context;
        }
        this.dbName  = dbName;
        this.cursorFactory = cursorFactory;
    }

    protected DatabaseOpenHelper getOpenHelper() {
        if (null == openHelper) {
            openHelper = new DatabaseOpenHelper(context, dbName, cursorFactory);
        }
        return openHelper;
    }

    public void invalidate() {
        daoSession = null;
        daoMaster = null;
        daoSessionSearch = null;
        daoMasterSearch = null;
        if (null != database) {
            database.close();
            database = null;
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        if (null == database || database.isReadOnly()) {
            invalidate();
            try {
                DatabaseOpenHelper databaseOpenHelper = getOpenHelper();
                if (databaseOpenHelper != null) {
                    database = databaseOpenHelper.getWritableDatabase();
                }
            } catch (Exception ex) {
                mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.LOCAL_DB, "ApplicationDao " +
                    "getWritableDatabase()", ex, null, true, false);
            }
        }
        return database;
    }

    public DaoMaster getDaoMaster() {
        if (null == daoMaster) {
            daoMaster = new DaoMaster(getWritableDatabase());
        }
        return daoMaster;
    }

    public com.pgi.network.models.DaoMaster getDaoMasterSearch() {
        if (null == daoMasterSearch) {
            daoMasterSearch = new com.pgi.network.models.DaoMaster(getWritableDatabase());
        }
        return daoMasterSearch;
    }

    public DaoSession getDaoSession() {
        if (null == daoSession) {
            daoSession = getDaoMaster().newSession();
        }
        return daoSession;
    }
    // for unit testing only
    public void setDaoSession(DaoSession ds) {
        daoSession = ds;
    }

    // for unit testing only
    public void setDaoSessionSearch(com.pgi.network.models.DaoSession ds) {
        daoSessionSearch = ds;
    }

    public com.pgi.network.models.DaoSession getDaoSessionSearch() {
        if (null == daoSessionSearch) {
            daoSessionSearch = getDaoMasterSearch().newSession();
        }
        return daoSessionSearch;
    }
    public MeetingRoomGetResultDao getMeetingResult() {
        return getDaoSession().getMeetingRoomGetResultDao();
    }

    public MeetingInfoResponseDao getMeetingInfo() {
        return getDaoSession().getMeetingInfoResponseDao();
    }

    public ClientInfoResponseDao getClientInfo() {
        return getDaoSession().getClientInfoResponseDao();
    }

    public ClientInfoResultDao getClientResult() {
        return getDaoSession().getClientInfoResultDao();
    }

    public ClientDetailsDao getClientDetail() {
        return getDaoSession().getClientDetailsDao();
    }

    public AddressDao getAddress() {
        return getDaoSession().getAddressDao();
    }

    public ClientContactDetailsDao getClientContactDetails() {
        return getDaoSession().getClientContactDetailsDao();
    }

    public CompanyDetailsDao getCompanyDetail() {
        return getDaoSession().getCompanyDetailsDao();
    }

    public MeetingRoomOptionDao getMeetingRoomOptions() {
        return getDaoSession().getMeetingRoomOptionDao();
    }

    public MeetingRoomUrlsDao getMeetingRoomUrls() {
        return getDaoSession().getMeetingRoomUrlsDao();
    }

    public PhoneInformationDao getPhoneInformations() {
        return getDaoSession().getPhoneInformationDao();
    }

    public ReservationOptionsDao getReservationOptions() {
        return getDaoSession().getReservationOptionsDao();
    }

    public MeetingRoomDao getMeetingRoom() {
        return getDaoSession().getMeetingRoomDao();
    }

    public PhoneDao getPhoneNumbers() {
        return getDaoSession().getPhoneDao();
    }
    public List<SearchResult> getAllRecentMeetings(){
        List<SearchResult>  searchResultList = null;
        try {
            searchResultList = getSearchResult().queryBuilder().where(SearchResultDao.Properties.Favorite.notEq(true)).list();
        } catch (Exception ex) {
            mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.LOCAL_DB, "ApplicationDao " +
                "getAllRecentMeetings()", ex, null, true, false);
        }
        return searchResultList;
    }

    private SearchResult getSearchMeetingForUrl(String furl) {
        return getSearchResult().queryBuilder().where(SearchResultDao.Properties.Furl.eq(furl)).limit(1).unique();
    }

    public long saveRecentMeeting(SearchResult item) {
        if (item != null) {
            SearchResult existingSearchResult = getSearchMeetingForUrl(item.getFurl());
            if (existingSearchResult != null) {
                item.id = existingSearchResult.id;
            }
            item.setFavorite(false);
            item.setModifiedDate(Long.toString(System.currentTimeMillis()));
            SearchResultDao dao = getSearchResult();
            return dao.insertOrReplace(item);
        }
        return 0;
    }

    public SearchResultDao getSearchResult() {

        return getDaoSessionSearch().getSearchResultDao();
    }

    public MeetingRoomDetailDao getMeetingDetails() {
        return  getDaoSession().getMeetingRoomDetailDao();
    }

    public UUIDDao getUUID() {
        return getDaoSession().getUUIDDao();
    }

    public LogsModelDao getElkLogs() {
        return getDaoSession().getLogsModelDao();
    }

    public void deleteSearchResult() {
        getDaoSessionSearch().runInTx(new Runnable() {
            @Override
            public void run() {
                getSearchResult().deleteAll();
            }
        });
    }

    public void deleteMeetingDetails() {
        getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                getMeetingDetails().deleteAll();
            }
        });
    }
    public void deleteAll() {
        getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                deleteAllModels();
                getElkLogs().deleteAll();
            }
        });
    }

    public void deleteAllExecptLogs() {
        getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                deleteAllModels();
            }
        });
    }

    public void deleteAllModels() {
        getClientResult().deleteAll();
        getClientInfo().deleteAll();
        getMeetingResult().deleteAll();
        getMeetingInfo().deleteAll();
        getClientDetail().deleteAll();
        getClientContactDetails().deleteAll();
        getCompanyDetail().deleteAll();
        getMeetingRoomOptions().deleteAll();
        getMeetingRoomUrls().deleteAll();
        getMeetingRoom().deleteAll();
        getMeetingDetails().deleteAll();
        getPhoneNumbers().deleteAll();
    }
    public void deleteSearchOnly() {
        getDaoSessionSearch().runInTx(new Runnable() {
            @Override
            public void run() {
                getSearchResult().deleteAll();
            }
        });
    }

    private final Context context;
    private final String dbName;
    private final SQLiteDatabase.CursorFactory cursorFactory;
    private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private com.pgi.network.models.DaoSession daoSessionSearch;
    private com.pgi.network.models.DaoMaster daoMasterSearch;

    private static final String DATABASE_NAME = "convergence-meeting-db";
    @Nullable private static ApplicationDao instance;
}
