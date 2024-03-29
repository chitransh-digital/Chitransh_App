package com.example.communityapp.ui.Business

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.communityapp.BaseActivity
import com.example.communityapp.R
import com.example.communityapp.data.models.Business
import com.example.communityapp.databinding.ActivityBusinessDetailsBinding
import com.example.communityapp.databinding.ActivityViewBusinessBinding
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class BusinessDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityBusinessDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setWindowsUp()
        val business = intent.getSerializableExtra("business") as? Business

        // Now you can use the business object to populate your UI or perform other operations
        if (business != null) {
            // For example, you can set the business name to a TextView
//            Toast.makeText(this, business.name, Toast.LENGTH_SHORT).show()

            binding.textViewBusinessName.text=business.name
            binding.textViewBusinessType.text=business.type
            binding.textViewBusinessDescription.text=business.desc
            binding.textViewBusinessAddress.text=business.address
//            Glide.with(this).load(business.images[0]).into(binding.businessImageView)

            val carousel: ImageCarousel = binding.businessImageView
            carousel.registerLifecycle(lifecycle)
            val imageList = mutableListOf<CarouselItem>()

            for(i in business.images){
                imageList.add(CarouselItem(i))
            }

            carousel.setData(imageList)

            binding.button1.setOnClickListener {

                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${business.contact}")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    ContextCompat.startActivity(this, intent, null)

            }

            binding.button2.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(business.link))
                startActivity(intent)
            }

            binding.button3.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(business.file))
                startActivity(intent)
            }

            if(business.coupon!="NA"){
                binding.llCoupon.visibility= View.VISIBLE
                binding.tvCoupon.text = business.coupon
            }

//            binding.textViewBusinessOwner.text = business.name

            // You can continue to populate other UI elements with business details
        } else {
            // Handle the case when business object is null
            Toast.makeText(this, "Error: Business data not found", Toast.LENGTH_SHORT).show()
            // You might want to finish() the activity or handle this case appropriately
        }

    }
}