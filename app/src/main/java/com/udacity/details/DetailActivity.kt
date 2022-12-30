package com.udacity.details

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.udacity.R
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.main.MainActivity

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailActivityViewModel by lazy {
        ViewModelProvider(
            this,
            DetailViewModelFactory(application)
        ).get(DetailActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)
        setSupportActionBar(binding.toolbar)
        binding.lifecycleOwner = this
        binding.contentDetail.viewModel = viewModel
        // extract intent
        viewModel.extractInfoFromBundle(intent.extras)

        // turn back MainActivity
        binding.contentDetail.okButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}
