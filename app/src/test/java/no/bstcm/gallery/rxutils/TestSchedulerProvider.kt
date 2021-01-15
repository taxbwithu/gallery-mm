package no.bstcm.gallery.rxutils

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestSchedulerProvider(
    main: Scheduler = Schedulers.trampoline(),
    io: Scheduler = Schedulers.trampoline(),
    computation: Scheduler = Schedulers.trampoline()
) : BaseSchedulerProvider(
    main,
    io,
    computation
)