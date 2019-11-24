package com.example.golda

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.golda.reviews.ReviewsActivity
import io.reactivex.Completable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var disposable = Disposables.disposed()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        disposable = Completable
            .timer(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                val intent = Intent(this, ReviewsActivity::class.java)
                startActivity(intent)
            }
    }

    override fun onDestroy() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        super.onDestroy()
    }
}

