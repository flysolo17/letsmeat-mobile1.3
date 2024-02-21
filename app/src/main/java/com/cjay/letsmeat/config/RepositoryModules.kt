package com.cjay.letsmeat.config


import com.cjay.letsmeat.repository.auth.AuthRepository
import com.cjay.letsmeat.repository.auth.AuthRepositoryImpl
import com.cjay.letsmeat.repository.cart.CartRepository
import com.cjay.letsmeat.repository.cart.CartRepositoryImpl
import com.cjay.letsmeat.repository.messages.MessagesRepository
import com.cjay.letsmeat.repository.messages.MessagesRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.cjay.letsmeat.repository.products.ProductRepository
import com.cjay.letsmeat.repository.products.ProductRepositoryImpl
import com.cjay.letsmeat.repository.transaction.TransactionRepository
import com.cjay.letsmeat.repository.transaction.TransactionRepositoryImpl

import dagger.Module
import dagger.Provides

import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModules {

    @Singleton
    @Provides
    fun providesAuthRepository(firestore: FirebaseFirestore,firebaseAuth: FirebaseAuth,storage: FirebaseStorage) : AuthRepository {
        return AuthRepositoryImpl(firestore,firebaseAuth,storage)
    }
    @Singleton
    @Provides
    fun provideProductRepository(firestore: FirebaseFirestore) : ProductRepository {
        return  ProductRepositoryImpl(firestore)
    }

    @Singleton
    @Provides
    fun provideCartRepository(firestore: FirebaseFirestore) : CartRepository {
        return  CartRepositoryImpl(firestore)
    }



    @Singleton
    @Provides
    fun provideTransactionRepository(firestore: FirebaseFirestore,storage: FirebaseStorage) : TransactionRepository {
        return  TransactionRepositoryImpl(firestore,storage)
    }


    @Singleton
    @Provides
    fun provideMessagesRepository(firestore: FirebaseFirestore) : MessagesRepository {
        return  MessagesRepositoryImpl(firestore)
    }
}