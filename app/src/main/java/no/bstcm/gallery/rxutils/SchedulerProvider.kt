package no.bstcm.gallery.rxutils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerProvider :
    BaseSchedulerProvider(AndroidSchedulers.mainThread(), Schedulers.io(), Schedulers.computation())