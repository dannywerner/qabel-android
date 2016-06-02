package de.qabel.qabelbox.dagger.modules;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;
import de.qabel.core.config.Identities;
import de.qabel.core.config.Identity;
import de.qabel.desktop.repository.IdentityRepository;
import de.qabel.desktop.repository.exception.EntityNotFoundExcepion;
import de.qabel.desktop.repository.exception.PersistenceException;
import de.qabel.qabelbox.activities.MainActivity;
import de.qabel.qabelbox.config.AppPreference;
import de.qabel.qabelbox.dagger.scopes.ActivityScope;
import de.qabel.qabelbox.navigation.MainNavigator;
import de.qabel.qabelbox.navigation.Navigator;
import de.qabel.qabelbox.exceptions.QblStorageException;
import de.qabel.qabelbox.storage.BoxManager;
import de.qabel.qabelbox.storage.BoxVolume;

import static de.qabel.qabelbox.activities.MainActivity.ACTIVE_IDENTITY;

@ActivityScope
@Module
public class MainActivityModule {

    private final MainActivity mainActivity;

    public MainActivityModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    public MainActivity provideMainActivity() {
        return mainActivity;
    }

    @Provides Identity provideActiveIdentity(IdentityRepository identityRepository,
                                             AppPreference sharedPreferences) {
        String identityKeyId = mainActivity.getIntent().getStringExtra(ACTIVE_IDENTITY);
        if (identityKeyId != null) {
            try {
                return identityRepository.find(identityKeyId);
            } catch (EntityNotFoundExcepion | PersistenceException entityNotFoundExcepion) {
                throw new IllegalStateException(
                        "Could not activate identity with key id " + identityKeyId);
            }
        } else {
            String keyId = sharedPreferences.getLastActiveIdentityKey();
            try {
                if(keyId != null){
                    return identityRepository.find(keyId);
                }else {
                    throw new PersistenceException("Not last active identity found");
                }
            } catch (EntityNotFoundExcepion | PersistenceException entityNotFoundExcepion) {
                try {
                    Identities identities = identityRepository.findAll();
                    if (identities.getIdentities().size() == 0) {
                        throw new IllegalStateException("No Identity available");
                    }
                    return identities.getIdentities().iterator().next();
                } catch (PersistenceException e) {
                    throw new IllegalStateException("Starting MainActivity without Identity");
                }
            }
        }
    }

    @Provides SharedPreferences provideSharedPreferences() {
        return mainActivity.getSharedPreferences(
                "LocalQabelService", Context.MODE_PRIVATE);
    }

    @ActivityScope @Provides Navigator provideNavigator(MainNavigator navigator) {
        return navigator;
    }

    @Provides
    BoxVolume provideBoxVolume(BoxManager boxManager, Identity activeIdentity){
        try {
            if (activeIdentity != null) {
                return boxManager.createBoxVolume(activeIdentity);
            }
        }catch (QblStorageException e){
            throw new IllegalStateException("Starting MainActivity without Volume");
        }
        return null;
    }


}
