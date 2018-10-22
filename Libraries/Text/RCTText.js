'use strict';

const UIManager = require('../ReactNative/UIManager');
const ReactNativeViewAttributes = require('../Components/View/ReactNativeViewAttributes');
const createReactNativeComponentClass = require('../Renderer/shims/createReactNativeComponentClass');

const viewConfig = {
  validAttributes: {
    ...ReactNativeViewAttributes.UIView,
    isHighlighted: true,
    numberOfLines: true,
    ellipsizeMode: true,
    allowFontScaling: true,
    maxFontSizeMultiplier: true,
    disabled: true,
    selectable: true,
    selectionColor: true,
    adjustsFontSizeToFit: true,
    minimumFontScale: true,
    textBreakStrategy: true,
    onTextLayout: true,
    onInlineViewLayout: true,
    dataDetectorType: true,
    onCustomActionItemClicked: true,
    customActionItem: true
  },
  directEventTypes: {
    topTextLayout: {
      registrationName: 'onTextLayout',
    },
    topInlineViewLayout: {
      registrationName: 'onInlineViewLayout',
    }
  },
  uiViewClassName: 'RCTText'
};

const RCTText = createReactNativeComponentClass(
  viewConfig.uiViewClassName,
  () => viewConfig
);

const RCTVirtualText =
  UIManager.getViewManagerConfig('RCTVirtualText') == null
    ? RCTText
    : createReactNativeComponentClass('RCTVirtualText', () => ({
        validAttributes: {
          ...ReactNativeViewAttributes.UIView,
          isHighlighted: true,
          maxFontSizeMultiplier: true,
        },
        uiViewClassName: 'RCTVirtualText',
      }));

module.exports = {
  RCTText: RCTText,
  RCTVirtualText: RCTVirtualText,
  viewConfig: viewConfig
}
