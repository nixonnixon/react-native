/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * <p>This source code is licensed under the MIT license found in the LICENSE file in the root
 * directory of this source tree.
 */
package com.facebook.react.views.text;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.IViewManagerWithChildren;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.yoga.YogaMeasureMode;
import java.util.Map;

import javax.annotation.Nullable;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.bridge.ReactContext;
import android.view.ActionMode;
import android.view.ActionMode.*;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Concrete class for {@link ReactTextAnchorViewManager} which represents view managers of anchor
 * {@code <Text>} nodes.
 */
@ReactModule(name = ReactTextViewManager.REACT_CLASS)
public class ReactTextViewManager
    extends ReactTextAnchorViewManager<ReactTextView, ReactTextShadowNode>
    implements IViewManagerWithChildren {

  @VisibleForTesting public static final String REACT_CLASS = "RCTText";

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

  public boolean needsCustomLayoutForChildren() {
    return true;
  }

  @Override
  public Object updateLocalData(
      ReactTextView view, ReactStylesDiffMap props, ReactStylesDiffMap localData) {
    ReadableMap attributedString = localData.getMap("attributedString");

    Spannable spanned =
        TextLayoutManager.getOrCreateSpannableForText(view.getContext(), attributedString);
    view.setSpanned(spanned);

    TextAttributeProps textViewProps = new TextAttributeProps(props);

    // TODO add textBreakStrategy prop into local Data
    int textBreakStrategy = Layout.BREAK_STRATEGY_HIGH_QUALITY;

    // TODO add justificationMode prop into local Data
    int justificationMode = Layout.JUSTIFICATION_MODE_NONE;

    return new ReactTextUpdate(
        spanned,
        -1, // TODO add this into local Data?
        false, // TODO add this into local Data
        textViewProps.getStartPadding(),
        textViewProps.getTopPadding(),
        textViewProps.getEndPadding(),
        textViewProps.getBottomPadding(),
        textViewProps.getTextAlign(),
        textBreakStrategy,
        justificationMode
      );
  }

  @Override
  public @Nullable Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.of(
        "topTextLayout", MapBuilder.of("registrationName", "onTextLayout"),
        "topInlineViewLayout", MapBuilder.of("registrationName", "onInlineViewLayout"),
        ReactTextViewCustomActionEvent.EVENT_NAME, MapBuilder.of("registrationName", "onCustomActionItemClicked"));
  }

  @Override
  public long measure(
      Context context,
      ReadableMap localData,
      ReadableMap props,
      ReadableMap state,
      float width,
      YogaMeasureMode widthMode,
      float height,
      YogaMeasureMode heightMode) {

    return TextLayoutManager.measureText(
        context, localData, props, width, widthMode, height, heightMode);
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
