# RxBase
- Wrap some **Rx Lib** that i normally use as **Android Library**.
- Integrate into android app efficiently.

# Download

[ ![Download](https://api.bintray.com/packages/ggg1234567/maven/rxbase/images/download.svg) ](https://bintray.com/ggg1234567/maven/rxbase/_latestVersion)

```gradle
implementation 'com.gg.rxbase:rxbase:<newest_verion>'
```



### UI Component

<details>

<summary>View contents</summary>

* [`RxBaseActivity`](#rxbaseactivity)

- RxBaseActivity

```java

import com.gg.rxbase.ui.RxBaseActivity;
import com.trello.navi2.Event;
import com.trello.rxlifecycle2.android.ActivityEvent;

public class XXXActivity extends RxBaseActivity {
    
    public XXXActivity() {
        
            /*
                no override oncreate() onResume ... if using  super.naviObserve(Event.XXX)
             */
        
            super.naviObserve(Event.CREATE).subscribe(new Consumer<Bundle>() {
                @Override
                public void accept(Bundle bundle) throws Exception {
                    setContentView(R.layout.main);
                }
            });
    
            // Counter that operates on screen only while resumed; automatically ends itself on destroy
            super.naviObserve(Event.RESUME)
                    .flatMap(new Function<Object, Observable<Long>>() {
    
                        @Override
                        public Observable<Long> apply(Object v) {
                            return Observable.interval(1, TimeUnit.SECONDS)
                                    .takeUntil(naviObserve(Event.PAUSE));
                        }
                    })
                    .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                    .startWith(-1L)
                    .observeOn(RxSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long count) {
                        }
                    });
        }
}

```

</details>


### Thread Component

<details>

<summary>View contents</summary>

* [RxSchedulers](#rxschedulers)

- RxSchedulers

```java
RxSchedulers.mainThread()
RxSchedulers.io()
RxSchedulers.single()
RxSchedulers.newThread()
RxSchedulers.computation()
RxSchedulers.trampoline()
RxSchedulers.from(Looper looper)
RxSchedulers.from(Executor executor)
```

</details>
