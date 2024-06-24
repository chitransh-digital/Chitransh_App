package com.example.communityapp.data.retrofit

import com.example.communityapp.data.models.LoginRequest
import com.example.communityapp.data.models.LoginRequestByID
import com.example.communityapp.data.models.LoginResponse
import com.example.communityapp.data.newModels.AddBusinessResponse
import com.example.communityapp.data.newModels.Business
import com.example.communityapp.data.newModels.BusinessResponse
import com.example.communityapp.data.newModels.CreateFamilyResponse
import com.example.communityapp.data.newModels.FamilyResponse
import com.example.communityapp.data.newModels.FeedsResponse
import com.example.communityapp.data.newModels.ImageResponse
import com.example.communityapp.data.newModels.JobsResponse
import com.example.communityapp.data.newModels.KaryakarniResponse
import com.example.communityapp.data.newModels.SignupRequest
import com.example.communityapp.data.newModels.SignupResponse
import com.example.communityapp.data.newModels.UpdateImage
import com.example.communityapp.data.newModels.addMember
import com.example.communityapp.data.newModels.addMemberReq
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomAPI {

    @POST("api/user/loginPhone")
    suspend fun loginPhone(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/user/loginPhoneFamilyID")
    suspend fun loginWithFamilyID(@Body request: LoginRequestByID): Response<LoginResponse>


    @POST("api/member/addMember/")
    suspend fun addMember(@Body request: SignupRequest): Response<SignupResponse>

    @Multipart
    @POST("api/image/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ImageResponse>

    @GET("api/member/viewFamilies")
    suspend fun getFamilyByContact(@Query("contact") contact: String): Response<FamilyResponse>

    @GET("api/member/viewFamilies")
    suspend fun getAllFamilies(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<FamilyResponse>

    @GET("api/feeds/getFeeds")
    suspend fun getFeeds(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<FeedsResponse>

    @GET("api/job/getAll")
    suspend fun getJobs(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<JobsResponse>

    @GET("api/business/getBusinesses")
    suspend fun getBusinesses(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<BusinessResponse>

    @POST("api/business/registerBusiness")
    suspend fun addBusiness(@Body request: Business): Response<AddBusinessResponse>

    @GET("api/karyakarni/getKaryakarnis")
    suspend fun getKaryakarni(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<KaryakarniResponse>

    @GET("api/karyakarni/getKaryakarnis")
    suspend fun getAllKaryakarni(): Response<KaryakarniResponse>

    @PATCH("api/member/update/{familyHash}/{memberHash}")
    suspend fun updateMember(@Body addMember: addMember,
                             @Path("familyHash") familyHash : String,
                             @Path("memberHash") memberHash : String): Response<SignupResponse>

    @DELETE("api/member/delete/{familyHash}/{memberHash}")
    suspend fun deleteMember(@Path("familyHash") familyHash : String,
                             @Path("memberHash") memberHash : String): Response<SignupResponse>

    @FormUrlEncoded
    @POST("api/member/createFamily")
    suspend fun createFamily(
        @Query("phone") phone: String,
        @Field("familyID") familyID: String,
        @Field("memberData") memberData: String
    ): Response<CreateFamilyResponse>

    @PATCH("api/member/update/{familyHash}/{memberHash}")
    suspend fun updateMemberImage(@Body profilePic: UpdateImage,
                                  @Path("familyHash") familyHash : String,
                                  @Path("memberHash") memberHash : String): Response<SignupResponse>

    @POST("api/member/addMember/{familyHash}")
    suspend fun addMember(@Body request: addMemberReq,
                          @Path("familyHash") familyHash : String): Response<SignupResponse>


}