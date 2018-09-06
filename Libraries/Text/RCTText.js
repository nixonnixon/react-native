'use strict';

const UIManager = require('UIManager');
const ReactNativeViewAttributes = require('ReactNativeViewAttributes');
const createReactNativeComponentClass = require('createReactNativeComponentClass');

const viewConfig = {
  validAttributes: {
    ...ReactNativeViewAttributes.UIView,
    isHighlighted: true,
    numberOfLines: true,
    ellipsizeMode: true,
    allowFontScaling: true,
    disabled: true,
    selectable: true,
    selectionColor: true,
    adjustsFontSizeToFit: true,
    minimumFontScale: true,
    textBreakStrategy: true,
  },
  uiViewClassName: 'RCTText'
};

const RCTText = createReactNativeComponentClass(
  viewConfig.uiViewClassName,
  () => viewConfig
);

const RCTVirtualText =
  UIManager.RCTVirtualText == null
    ? RCTText
    : createReactNativeComponentClass('RCTVirtualText', () => ({
        validAttributes: {
          ...ReactNativeViewAttributes.UIView,
          isHighlighted: true
        },
        uiViewClassName: 'RCTVirtualText'
      }));

module.exports = {
  RCTText: RCTText,
  RCTVirtualText: RCTVirtualText,
  viewConfig: viewConfig
}
