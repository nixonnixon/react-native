/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.views.text;

import android.text.Spannable;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.bridge.ReactContext;
import android.view.ActionMode;
import android.view.ActionMode.*;
import android.view.Menu;
import android.view.MenuItem;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Concrete class for {@link ReactTextAnchorViewManager} which represents view managers of anchor
 * {@code <Text>} nodes.
 */
@ReactModule(name = ReactTextViewManager.REACT_CLASS)
public class ReactTextViewManager
    extends ReactTextAnchorViewManager<ReactTextView, ReactTextShadowNode> {

  @VisibleForTesting
  public static final String REACT_CLASS = "RCTText";

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  public ReactTextView createViewInstance(ThemedReactContext context) {
    return new ReactTextView(context);
  }

  @Override
  public void updateExtraData(ReactTextView view, Object extraData) {
    ReactTextUpdate update = (ReactTextUpdate) extraData;
    if (update.containsImages()) {
      Spannable spannable = update.getText();
      TextInlineImageSpan.possiblyUpdateInlineImageSpans(spannable, view);
    }
    view.setText(update);
  }

  @Override
  public ReactTextShadowNode createShadowNodeInstance() {
    return new ReactTextShadowNode();
  }

  @Override
  public Class<ReactTextShadowNode> getShadowNodeClass() {
    return ReactTextShadowNode.class;
  }

  @Override
  protected void onAfterUpdateTransaction(ReactTextView view) {
    super.onAfterUpdateTransaction(view);
    view.updateView();
  }

  @Nullable
  @Override
  public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.<String, Object>builder()
        .put(ReactTextViewCustomActionEvent.EVENT_NAME, MapBuilder.of("registrationName", "onCustomActionItemClicked"))
        .build();
  }

  @ReactProp(name = "customActionItem")
  public void setCustomActionItem(final ReactTextView view, @Nullable final String customActionItem) {
    if (customActionItem != null) {
      view.setCustomSelectionActionModeCallback(new Callback() {
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Called when action mode is first created. The menu supplied
            // will be used to generate action buttons for the action mode

            // Here is an example MenuItem
            menu.add(0, 0, 0, customActionItem);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // Called when an action mode is about to be exited and
            // destroyed
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case 0:
                    int min = 0;
                    int max = view.getText().length();
                    if (view.isFocused()) {
                        final int selStart = view.getSelectionStart();
                        final int selEnd = view.getSelectionEnd();

                        min = Math.max(0, Math.min(selStart, selEnd));
                        max = Math.max(0, Math.max(selStart, selEnd));
                    }
                    // Perform your definition lookup with the selected text
                    final CharSequence selectedText = view.getText().subSequence(min, max);
                    // Finish and close the ActionMode
                    ReactContext reactContext = (ReactContext) view.getContext();
                    EventDispatcher eventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();

                    eventDispatcher.dispatchEvent(
                      new ReactTextViewCustomActionEvent(
                        view.getId(),
                          min,
                          max
                    ));

                    mode.finish();
                    return true;
                default:
                    break;
            }
            return false;
        }

    });
    } else {
      view.setCustomSelectionActionModeCallback(null);
    }
  }
}
