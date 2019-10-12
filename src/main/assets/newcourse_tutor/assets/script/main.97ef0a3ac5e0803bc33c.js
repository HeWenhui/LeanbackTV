(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["main"],{

/***/ 162:
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__.p + "assets/img/loading.b11c270.gif";

/***/ }),

/***/ 163:
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony import */ var _node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_0_node_modules_css_loader_dist_cjs_js_ref_8_1_node_modules_vue_loader_lib_loaders_stylePostLoader_js_node_modules_postcss_loader_src_index_js_ref_8_2_node_modules_sass_loader_dist_cjs_js_ref_8_3_node_modules_vue_loader_lib_index_js_vue_loader_options_main_vue_vue_type_style_index_0_id_4d935864_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(31);
/* harmony import */ var _node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_0_node_modules_css_loader_dist_cjs_js_ref_8_1_node_modules_vue_loader_lib_loaders_stylePostLoader_js_node_modules_postcss_loader_src_index_js_ref_8_2_node_modules_sass_loader_dist_cjs_js_ref_8_3_node_modules_vue_loader_lib_index_js_vue_loader_options_main_vue_vue_type_style_index_0_id_4d935864_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_0_node_modules_css_loader_dist_cjs_js_ref_8_1_node_modules_vue_loader_lib_loaders_stylePostLoader_js_node_modules_postcss_loader_src_index_js_ref_8_2_node_modules_sass_loader_dist_cjs_js_ref_8_3_node_modules_vue_loader_lib_index_js_vue_loader_options_main_vue_vue_type_style_index_0_id_4d935864_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0__);
/* unused harmony reexport * */
 /* unused harmony default export */ var _unused_webpack_default_export = (_node_modules_mini_css_extract_plugin_dist_loader_js_ref_8_0_node_modules_css_loader_dist_cjs_js_ref_8_1_node_modules_vue_loader_lib_loaders_stylePostLoader_js_node_modules_postcss_loader_src_index_js_ref_8_2_node_modules_sass_loader_dist_cjs_js_ref_8_3_node_modules_vue_loader_lib_index_js_vue_loader_options_main_vue_vue_type_style_index_0_id_4d935864_lang_scss_scoped_true___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 166:
/***/ (function(module, exports, __webpack_require__) {

// extracted by mini-css-extract-plugin

/***/ }),

/***/ 167:
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);

// EXTERNAL MODULE: ./node_modules/vue/dist/vue.runtime.js
var vue_runtime = __webpack_require__(21);
var vue_runtime_default = /*#__PURE__*/__webpack_require__.n(vue_runtime);

// CONCATENATED MODULE: /Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!/Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/vue-loader/lib??vue-loader-options!./src/App.vue?vue&type=template&id=02c6690d&
var Appvue_type_template_id_02c6690d_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{class:['result-root', _vm.getter_resultBackgroundClass]},[_c('div',{staticClass:"result-info"},[_vm._v(_vm._s(_vm.getter_resultPageText))]),_vm._v(" "),_c('div',{staticClass:"close",on:{"click":_vm.handleCloseWebPage}}),_vm._v(" "),_c('div',{ref:"swiperRef",staticClass:"swiper"},[_c('div',{staticClass:"swiper-list-group"},[_c('div',{staticClass:"swiper-item"},[_c('div',{staticClass:"answer-info"},[_vm._m(0),_vm._v(" "),_c('div',{staticClass:"content"},_vm._l((_vm.getter_studentAnswerList),function(data,index){return _c('div',{key:data.testId,staticClass:"answer-item"},[_c('div',{staticClass:"i index"},[_vm._v(_vm._s(index + 1))]),_vm._v(" "),_c('div',{staticClass:"i expect"},[_c('div',{staticClass:"answer-wrap"},_vm._l((data.rightAnswer),function(item){return _c('div',{key:item},[_vm._v(_vm._s(item))])}),0)]),_vm._v(" "),_c('div',{staticClass:"i yours"},[_c('div',{staticClass:"answer-wrap"},_vm._l((data.stuAnswer),function(item,i){return _c('div',{key:i,class:{ r: item.right*1 === 1, w: item.right*1 === 0, sf: !item.answer }},[_vm._v("\n                    "+_vm._s(item.answer ? item.answer : '未作答')+"\n                  ")])}),0)]),_vm._v(" "),_c('div',{staticClass:"i result",class:{ r: data.isRight*1 === 2, p: data.isRight*1 === 1, w: data.isRight*1 === 0 }})])}),0)])]),_vm._v(" "),_c('div',{staticClass:"swiper-item"},[_c('div',{staticClass:"pie-info"},[_c('div',{staticClass:"header"},[_c('span',{staticClass:"icon",class:_vm.getter_resultBackgroundClass}),_vm._v("\n            组内答题情况\n          ")]),_vm._v(" "),_c('div',{ref:"chartsRef",staticClass:"pie"},[_c('canvas',{attrs:{"id":"canvas"}})])])]),_vm._v(" "),_c('div',{staticClass:"swiper-item",staticStyle:{"background-color":"rgba(0, 0, 0, 0)"}},[_c('div',{staticClass:"top-info"},[_c('div',{staticClass:"top-15 left",class:_vm.getter_resultBackgroundClass},[_c('div',{staticClass:"header"},[_vm._v("组内前15名")]),_vm._v(" "),_c('div',{staticClass:"list"},[_vm._m(1),_vm._v(" "),_c('div',{staticClass:"list-content"},_vm._l((_vm.getter_teamGroupTop15Data),function(data,index){return _c('div',{key:data.stuId,staticClass:"item"},[_c('div',{staticClass:"index",class:{ one: index === 0, two: index === 1, three: index ===2 }},[_vm._v("\n                  "+_vm._s(index > 2 ? index + 1 : '')+"\n                  ")]),_vm._v(" "),_c('div',{staticClass:"name",class:{ own: data.isMe*1 === 1 }},[_vm._v(_vm._s(data.stuName))]),_vm._v(" "),_c('div',{staticClass:"status",class:{ own: data.isMe*1 === 1 }},[_vm._v(_vm._s(data.correctRate))])])}),0)])]),_vm._v(" "),_c('div',{staticClass:"top-15 right",class:_vm.getter_resultBackgroundClass},[_c('div',{staticClass:"header"},[_vm._v("班内前15名")]),_vm._v(" "),_c('div',{staticClass:"list"},[_vm._m(2),_vm._v(" "),_c('div',{staticClass:"list-content"},_vm._l((_vm.getter_teamClassTop15Data),function(data,index){return _c('div',{key:data.stuId,staticClass:"item"},[_c('div',{staticClass:"index",class:{ one: index === 0, two: index === 1, three: index ===2 }},[_vm._v("\n                  "+_vm._s(index > 2 ? index + 1 : '')+"\n                  ")]),_vm._v(" "),_c('div',{staticClass:"name",class:{ own: data.isMe*1 === 1 }},[_vm._v(_vm._s(data.stuName))]),_vm._v(" "),_c('div',{staticClass:"status",class:{ own: data.isMe*1 === 1 }},[_vm._v(_vm._s(data.correctRate))])])}),0)])])])])])]),_vm._v(" "),_c('div',{staticClass:"swiper-pointer-group"},_vm._l((3),function(i){return _c('span',{key:i,staticClass:"pointer",class:{ active: _vm.var_currentPointIndex === i - 1 }})}),0)])}
var staticRenderFns = [function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"header"},[_c('div',{staticClass:"i index"},[_vm._v("题号")]),_vm._v(" "),_c('div',{staticClass:"i expect"},[_vm._v("正确答案")]),_vm._v(" "),_c('div',{staticClass:"i yours"},[_vm._v("你的答案")]),_vm._v(" "),_c('div',{staticClass:"i result"},[_vm._v("答案情况")])])},function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"list-header"},[_c('div',{staticClass:"index"},[_vm._v("排名")]),_vm._v(" "),_c('div',{staticClass:"name"},[_vm._v("姓名")]),_vm._v(" "),_c('div',{staticClass:"status"},[_vm._v("正确率")])])},function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"list-header"},[_c('div',{staticClass:"index"},[_vm._v("排名")]),_vm._v(" "),_c('div',{staticClass:"name"},[_vm._v("姓名")]),_vm._v(" "),_c('div',{staticClass:"status"},[_vm._v("正确率")])])}]


// CONCATENATED MODULE: ./src/App.vue?vue&type=template&id=02c6690d&

// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/core-js-stable/instance/map.js
var map = __webpack_require__(78);
var map_default = /*#__PURE__*/__webpack_require__.n(map);

// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/core-js-stable/set-interval.js
var set_interval = __webpack_require__(79);
var set_interval_default = /*#__PURE__*/__webpack_require__.n(set_interval);

// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/regenerator/index.js
var regenerator = __webpack_require__(0);
var regenerator_default = /*#__PURE__*/__webpack_require__.n(regenerator);

// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/core-js-stable/instance/bind.js
var bind = __webpack_require__(80);
var bind_default = /*#__PURE__*/__webpack_require__.n(bind);

// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/helpers/asyncToGenerator.js
var asyncToGenerator = __webpack_require__(9);
var asyncToGenerator_default = /*#__PURE__*/__webpack_require__.n(asyncToGenerator);

// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/core-js-stable/set-timeout.js
var set_timeout = __webpack_require__(33);
var set_timeout_default = /*#__PURE__*/__webpack_require__.n(set_timeout);

// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/core-js-stable/promise.js
var promise = __webpack_require__(5);
var promise_default = /*#__PURE__*/__webpack_require__.n(promise);

// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/core-js-stable/json/stringify.js
var stringify = __webpack_require__(47);
var stringify_default = /*#__PURE__*/__webpack_require__.n(stringify);

// EXTERNAL MODULE: ./node_modules/querystringify/index.js
var querystringify = __webpack_require__(20);
var querystringify_default = /*#__PURE__*/__webpack_require__.n(querystringify);

// CONCATENATED MODULE: ./src/model/formatParams.js

var isDev = "production" === 'development';
var search = !isDev ? window.location.search : // `?isForce=0&stuId=12491&stuCouId=9642934&packageId=41312&liveId=340493&packageSource=2&packageAttr=1&releasedPageInfos=[%7B%2242689%22:[%2222%22,%2220276%22]%7D,%7B%2242688%22:[%2222%22,%22100922%22]%7D]&isPlayBack=0&classId=30951&classTestId=0&srcTypes=22,22&testIds=20276,100922&educationStage=3&teamId=1&nonce=12491_1547464034314&entranceTime=1547464034`
// ?stuId=99916&liveId=255783&stuCouId=17558382&classId=99772&teamId=1&packageId=38593&packageSource=2&packageAttr=1&classTestId=0&releasedPageInfos=[{"42151":["19","489440"]}]&isPlayBack=0&educationStage=3&isShowTeamPk=0&nonce=99916_1550460486038
// ?stuId=12491&liveId=340493&stuCouId=9642934&classId=30951&teamId=1&packageId=41312&packageSource=2&packageAttr=1&classTestId=0&releasedPageInfos=[{%2242689%22:[%2222%22,%2220276%22]},{%2242688%22:[%2222%22,%22100922%22]}]&isPlayBack=0&educationStage=3&isShowTeamPk=0&nonce=12491_1547464034314
// 线上
"?stuId=11681&liveId=346352&stuCouId=21887914&classId=212461&teamId=1&packageId=150699&packageSource=2&packageAttr=4&releasedPageInfos=%5B%7B\"160576\":%5B\"0\",\"3285695\"%5D%7D%5D&classTestId=0&educationStage=0&isPlayBack=1&nonce=60DF742A-6766-4ABD-9BDD-E046FFDF2B0A";
console.log('当前 url query 参数:', querystringify_default.a.parse(search));

var _qs$parse = querystringify_default.a.parse(search),
    forceSubmit = _qs$parse.forceSubmit,
    stuId = _qs$parse.stuId,
    stuCouId = _qs$parse.stuCouId,
    packageId = _qs$parse.packageId,
    liveId = _qs$parse.liveId,
    packageSource = _qs$parse.packageSource,
    packageAttr = _qs$parse.packageAttr,
    releasedPageInfos = _qs$parse.releasedPageInfos,
    isPlayBack = _qs$parse.isPlayBack,
    classId = _qs$parse.classId,
    classTestId = _qs$parse.classTestId,
    educationStage = _qs$parse.educationStage,
    teamId = _qs$parse.teamId,
    nonce = _qs$parse.nonce,
    chs = _qs$parse.chs;

var releasedPageInfosObject = JSON.parse(releasedPageInfos);
var srcTypesData = [];
var testIdsData = [];

for (var formatParams_i = 0; formatParams_i < releasedPageInfosObject.length; formatParams_i++) {
  var item = releasedPageInfosObject[formatParams_i];

  for (var j in item) {
    srcTypesData.push(item[j][0]);
    testIdsData.push(item[j][1]);
  }
}

var srcTypes = srcTypesData.join(',');
var testIds = testIdsData.join(',');
var queryData = {
  forceSubmit: forceSubmit,
  stuId: stuId,
  stuCouId: stuCouId,
  packageId: packageId,
  liveId: liveId,
  packageSource: packageSource,
  packageAttr: packageAttr,
  releasedPageInfos: releasedPageInfos,
  isPlayBack: isPlayBack,
  classId: classId,
  classTestId: classTestId,
  srcTypes: srcTypes,
  testIds: testIds,
  educationStage: educationStage,
  teamId: teamId,
  nonce: nonce,
  chs: chs
};
console.log('解析完成的 query 参数:', queryData);
/* harmony default export */ var formatParams = (queryData);
// EXTERNAL MODULE: ./node_modules/axios/index.js
var axios = __webpack_require__(81);
var axios_default = /*#__PURE__*/__webpack_require__.n(axios);

// CONCATENATED MODULE: ./src/model/createApi.js



var createRequest = axios_default.a.create({
  // 安卓本地的
  baseURL: 'https://live.xueersi.com/science/Tutorship/',
  transformRequest: [function (data) {
    return querystringify_default.a.stringify(data);
  }]
});

var createApi_request = function request(req) {
  return new promise_default.a(function (resolve, reject) {
    req.then(function (res) {
      if (res.status === 200) {
        resolve(res.data);
      } else {
        reject(new Error('API 接口请求错误！ Code: ' + res.status));
      }
    })["catch"](function (err) {
      return reject(err);
    });
  });
}; // 获取作答结果接口


var API_getStudentAnsweredResultData = function API_getStudentAnsweredResultData(params) {
  return createApi_request(createRequest.get('/getStuTestResult', {
    params: params
  }));
}; // 组内答题情况饼状图统计接口


var API_getTeamAnwseredResultPieData = function API_getTeamAnwseredResultPieData(params) {
  return createApi_request(createRequest.get('/teamStatistics', {
    params: params
  }));
}; // 学生排名情况列表（组内前15名&班内前15名）


var API_getTeamTop15ListData = function API_getTeamTop15ListData(params) {
  return createApi_request(createRequest.get('/top15List', {
    params: params
  }));
};

/* harmony default export */ var createApi = ({
  API_getStudentAnsweredResultData: API_getStudentAnsweredResultData,
  API_getTeamAnwseredResultPieData: API_getTeamAnwseredResultPieData,
  API_getTeamTop15ListData: API_getTeamTop15ListData
});
// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/core-js-stable/date/now.js
var now = __webpack_require__(48);
var now_default = /*#__PURE__*/__webpack_require__.n(now);

// CONCATENATED MODULE: ./src/utils/swiper.js


var addTransition = function addTransition(el) {
  el.style.transition = 'transform 0.15s linear';
  el.style.webkitTransition = 'transform 0.15s linear';
};

var removeTransition = function removeTransition(el) {
  el.style.transition = 'none';
  el.style.webkitTransition = 'none';
};

var setTranslate = function setTranslate(el, x) {
  el.style.transform = "translate3d(".concat(x, ", 0, 0)");
  el.style.webkitTransform = "translate3d(".concat(x, ", 0, 0)");
};

function swiper(el, distance, cb) {
  var moveGroup = el.children[0];
  var moveLength = moveGroup.children.length;
  var startX = 0;
  var startY = 0;
  var index = 0;
  var isMoved = false;
  var dx = 0;
  var dy = 0;
  var diff = 0;
  var startTime = 0;
  setTranslate(moveGroup, -index * distance / 100 + 'rem');
  el.addEventListener('touchstart', function (e) {
    startX = e.touches[0].pageX;
    startY = e.touches[0].pageY;
    startTime = now_default()();
  });
  el.addEventListener('touchmove', function (e) {
    var _e$touches$ = e.touches[0],
        pageX = _e$touches$.pageX,
        pageY = _e$touches$.pageY;
    dx = pageX - startX;
    dy = pageY - startY;
    diff = Math.abs(dx) - Math.abs(dy);

    if (diff < 10) {
      return false;
    } else {
      e.preventDefault();
    }

    removeTransition(moveGroup);
    var d = -index * distance + dx;
    if (dx > 0 && index === 0) d = 0;
    if (dx < 0 && index === moveLength - 1) d = (moveLength - 1) * -distance;
    setTranslate(moveGroup, d / 100 + 'rem');
    isMoved = true;
  });
  el.addEventListener('touchend', function () {
    if (isMoved && Math.abs(dx) > distance / 6 && now_default()() - startTime > 50) {
      if (dx > 0) index--;
      if (dx < 0) index++;
      index = Math.max(0, index);
      index = Math.min(index, moveLength - 1);
    }

    addTransition(moveGroup);
    setTranslate(moveGroup, -index * distance / 100 + 'rem');
    cb && cb(index);
    startX = startY = dx = dy = 0;
    isMoved = false;
  });
}
// EXTERNAL MODULE: ./node_modules/@babel/runtime-corejs3/core-js-stable/instance/fill.js
var fill = __webpack_require__(49);
var fill_default = /*#__PURE__*/__webpack_require__.n(fill);

// CONCATENATED MODULE: ./src/utils/createPie.js

var base = {
  wrong: {
    title: '全部错误',
    textAlign: 'right',
    line: {
      start: [7.76, 0.7],
      end: [9.92, 0.7]
    }
  },
  partRight: {
    title: '部分正确',
    textAlign: 'right',
    line: {
      start: [7.76, 2],
      end: [9.92, 2]
    }
  },
  right: {
    title: '全部正确',
    textAlign: 'left',
    line: {
      start: [3.76, 1.4],
      end: [1.6, 1.4]
    }
  }
};
var colorMap = {
  right: '#28B3AC',
  partRight: '#FFBB67',
  wrong: '#FF7878'
};

var createPie_initChartInfo = function initChartInfo(ctx, color, preset, type, scale) {
  var r = 0.12 * scale;
  var x = base[type].line.start[0] * scale;
  var y = base[type].line.start[1] * scale;
  ctx.save();
  ctx.fillStyle = color;
  ctx.arc(x, y, r, 0, 2 * Math.PI);

  fill_default()(ctx).call(ctx);

  ctx.restore();
  var endx = base[type].line.end[0] * scale;
  var endy = base[type].line.end[1] * scale;
  var text = base[type].title;
  var presetNum = '0%';

  if (preset * 1 === 1) {
    presetNum = '100%';
  } else if (!(preset * 1)) {
    presetNum = '0%';
  } else {
    presetNum = Math.round(preset * 100) + '%';
  }

  ctx.save();
  ctx.strokeStyle = color;
  ctx.fillStyle = color;
  ctx.lineWidth = 0.04 * scale;
  ctx.textAlign = base[type].textAlign;
  ctx.font = 0.3 * scale + 'px Arial';
  ctx.beginPath();
  ctx.lineTo(x, y);
  ctx.lineTo(endx, endy);
  ctx.stroke();
  ctx.fillText(presetNum, endx, endy - 0.13 * scale);
  ctx.fillText(text, endx, endy + 0.35 * scale);
  ctx.restore();
};

function pie(el, data) {
  var canvas = el.children[0];
  var ctx = canvas.getContext('2d');
  var scale = document.documentElement.clientWidth / 1334 * 100 * 2;
  var W = canvas.width = 11.94 * scale;
  var H = canvas.height = 3 * scale;
  var centerX = W / 2;
  var centerY = H / 2;
  var from = 0.6 * scale;
  var to = 1.38 * scale;
  var start = 3 * Math.PI / 2;
  var end = start;
  var color = '#fff';
  ctx.clearRect(0, 0, W, H);

  for (var i = 0; i < data.length; i++) {
    var preset = Math.PI * 2 * data[i].preset;
    end = start + preset;
    color = colorMap[data[i].type];
    ctx.save();
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(centerX, centerY, to, end, start, true);
    ctx.arc(centerX, centerY, from, start, end);
    ctx.closePath();

    fill_default()(ctx).call(ctx);

    ctx.restore();
    start = end;
    createPie_initChartInfo(ctx, color, data[i].preset, data[i].type, scale);
  }
}
// CONCATENATED MODULE: /Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!/Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/vue-loader/lib??vue-loader-options!./src/components/loading/main.vue?vue&type=template&id=4d935864&scoped=true&
var mainvue_type_template_id_4d935864_scoped_true_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.visible),expression:"visible"}],staticClass:"loading"},[_c('img',{staticClass:"img",attrs:{"src":__webpack_require__(162)}}),_vm._v(" "),_vm._m(0),_vm._v(" "),_c('div',{staticClass:"meta"},[_vm._v(_vm._s(_vm.message))])])}
var mainvue_type_template_id_4d935864_scoped_true_staticRenderFns = [function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"bar"},[_c('div',{staticClass:"percent"})])}]


// CONCATENATED MODULE: ./src/components/loading/main.vue?vue&type=template&id=4d935864&scoped=true&

// CONCATENATED MODULE: /Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/babel-loader/lib??ref--0!/Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/vue-loader/lib??vue-loader-options!./src/components/loading/main.vue?vue&type=script&lang=js&
//
//
//
//
//
//
//
//
//
//
/* harmony default export */ var mainvue_type_script_lang_js_ = ({
  data: function data() {
    return {
      visible: false,
      message: '加载中，请稍后...'
    };
  },
  methods: {
    open: function open() {
      this.visible = true;
    },
    close: function close() {
      this.visible = true;
      this.$el.parentNode.removeChild(this.$el);
      this.$destroy(true);
    }
  }
});
// CONCATENATED MODULE: ./src/components/loading/main.vue?vue&type=script&lang=js&
 /* harmony default export */ var loading_mainvue_type_script_lang_js_ = (mainvue_type_script_lang_js_); 
// EXTERNAL MODULE: ./src/components/loading/main.vue?vue&type=style&index=0&id=4d935864&lang=scss&scoped=true&
var mainvue_type_style_index_0_id_4d935864_lang_scss_scoped_true_ = __webpack_require__(163);

// EXTERNAL MODULE: /Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/vue-loader/lib/runtime/componentNormalizer.js
var componentNormalizer = __webpack_require__(32);

// CONCATENATED MODULE: ./src/components/loading/main.vue






/* normalize component */

var component = Object(componentNormalizer["a" /* default */])(
  loading_mainvue_type_script_lang_js_,
  mainvue_type_template_id_4d935864_scoped_true_render,
  mainvue_type_template_id_4d935864_scoped_true_staticRenderFns,
  false,
  null,
  "4d935864",
  null
  
)

/* harmony default export */ var main = (component.exports);
// CONCATENATED MODULE: ./src/components/loading/index.js





var Constructor = vue_runtime_default.a.extend(main);

var loading_requestAll = function requestAll(arr) {
  return promise_default.a.all(arr);
};

var loading_$loading = function $loading() {
  var options = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
  return new promise_default.a(
  /*#__PURE__*/
  function () {
    var _ref = asyncToGenerator_default()(
    /*#__PURE__*/
    regenerator_default.a.mark(function _callee(resolve) {
      var instance, _options$requestList, requestList;

      return regenerator_default.a.wrap(function _callee$(_context) {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              instance = new Constructor({
                data: options
              });
              _options$requestList = options.requestList, requestList = _options$requestList === void 0 ? [] : _options$requestList;
              instance.$mount();
              document.body.appendChild(instance.$el);
              instance.open();
              _context.next = 7;
              return loading_requestAll(requestList);

            case 7:
              instance.close();
              resolve();

            case 9:
            case "end":
              return _context.stop();
          }
        }
      }, _callee);
    }));

    return function (_x) {
      return _ref.apply(this, arguments);
    };
  }());
};


// CONCATENATED MODULE: /Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/babel-loader/lib??ref--0!/Users/zhangting/Downloads/git/GitLab/fe-hybride-stage/node_modules/vue-loader/lib??vue-loader-options!./src/App.vue?vue&type=script&lang=js&








//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//






var Appvue_type_script_lang_js_postToAPP = function postToAPP(data) {
  if (window.webkit && window.webkit.messageHandlers.xesApp) {
    window.webkit.messageHandlers.xesApp.postMessage(stringify_default()(data));
  } else if (window.xesApp) {
    window.xesApp[data.methodName](stringify_default()(data));
  }
};

var isForceSubmit = !!(formatParams.forceSubmit * 1);
var Appvue_type_script_lang_js_isPlayBack = !!(formatParams.isPlayBack * 1);

var Appvue_type_script_lang_js_sleep = function sleep(d) {
  return new promise_default.a(function (resolve) {
    return set_timeout_default()(resolve, d);
  });
};

/* harmony default export */ var Appvue_type_script_lang_js_ = ({
  data: function data() {
    return {
      var_initReady: false,
      var_isForceSubmit: isForceSubmit,
      var_isPlayBack: Appvue_type_script_lang_js_isPlayBack,
      var_teamAnsweredPieData: null,
      var_teamTop15ListData: {},
      var_studentAnsweredResultData: {},
      var_chartsData: null,
      var_currentPointIndex: 0,
      var_requestTeamPieDataTimes: 0,
      var_requestTeamTop15Times: 0,
      var_maxRequestTimes: 15,
      var_teacherHasTakeUp: false
    };
  },
  computed: {
    getter_isShowResultPage: function getter_isShowResultPage() {
      return this.var_studentAnsweredResultData && this.var_teamAnsweredPieData;
    },
    getter_resutlPageTypeData: function getter_resutlPageTypeData() {
      var _this$var_studentAnsw = this.var_studentAnsweredResultData,
          gold = _this$var_studentAnsw.gold,
          type = _this$var_studentAnsw.type;
      var backgroundClass = '',
          resultText = '';

      switch (type) {
        case 0:
          backgroundClass = 'w';
          resultText = '很遗憾答错了，没有获得金币哟！';
          break;

        case 1:
          backgroundClass = 'r';
          resultText = "\u606D\u559C\u4F60\u7B54\u5BF9\u4E86\uFF0C\u83B7\u5F97".concat(gold, "\u4E2A\u91D1\u5E01\uFF01");
          break;

        case 2:
          backgroundClass = 'p';
          resultText = "\u90E8\u5206\u6B63\u786E\uFF0C\u83B7\u5F97".concat(gold, "\u4E2A\u91D1\u5E01\uFF01");
          break;
      }

      return {
        backgroundClass: backgroundClass,
        resultText: resultText
      };
    },
    getter_resultBackgroundClass: function getter_resultBackgroundClass() {
      return this.getter_resutlPageTypeData.backgroundClass;
    },
    getter_resultPageText: function getter_resultPageText() {
      return this.getter_resutlPageTypeData.resultText;
    },
    getter_studentAnswerList: function getter_studentAnswerList() {
      return this.var_studentAnsweredResultData.answerLists || [];
    },
    getter_teamGroupTop15Data: function getter_teamGroupTop15Data() {
      return this.var_teamTop15ListData ? this.var_teamTop15ListData.teamRank : [];
    },
    getter_teamClassTop15Data: function getter_teamClassTop15Data() {
      return this.var_teamTop15ListData ? this.var_teamTop15ListData.classRank : [];
    }
  },
  created: function created() {
    window._fn_({
      fontSize: 100,
      width: 1334
    });
  },
  mounted: function () {
    var _mounted = asyncToGenerator_default()(
    /*#__PURE__*/
    regenerator_default.a.mark(function _callee() {
      var _context;

      return regenerator_default.a.wrap(function _callee$(_context2) {
        while (1) {
          switch (_context2.prev = _context2.next) {
            case 0:
              window.__CLIENT_SUBMIT__ = bind_default()(_context = this.handleTeacherTackUp).call(_context, this);

              if (window.location.hash === '#hasTakeUp') {
                this.var_teacherHasTakeUp = true;
              }

              _context2.next = 4;
              return this.initResultPage();

            case 4:
              Appvue_type_script_lang_js_postToAPP({
                methodName: 'resultPageLoaded'
              });

            case 5:
            case "end":
              return _context2.stop();
          }
        }
      }, _callee, this);
    }));

    function mounted() {
      return _mounted.apply(this, arguments);
    }

    return mounted;
  }(),
  methods: {
    initResultPage: function () {
      var _initResultPage = asyncToGenerator_default()(
      /*#__PURE__*/
      regenerator_default.a.mark(function _callee2() {
        var _this = this;

        return regenerator_default.a.wrap(function _callee2$(_context3) {
          while (1) {
            switch (_context3.prev = _context3.next) {
              case 0:
                _context3.next = 2;
                return this.initSwiper();

              case 2:
                _context3.next = 4;
                return loading_$loading({
                  requestList: [this.requestAllResultPageData()]
                });

              case 4:
                this.var_initReady = true;

                set_interval_default()(function () {
                  if (++_this.var_requestTeamPieDataTimes < _this.var_maxRequestTimes) {
                    _this.requestTeamAnsweredPieData();
                  }

                  if (_this.var_isForceSubmit || _this.var_teacherHasTakeUp) {
                    if (++_this.var_requestTeamTop15Times < _this.var_maxRequestTimes) {
                      _this.requestStudentAnwseredResultData();
                    }
                  }
                }, 8000);

              case 6:
              case "end":
                return _context3.stop();
            }
          }
        }, _callee2, this);
      }));

      function initResultPage() {
        return _initResultPage.apply(this, arguments);
      }

      return initResultPage;
    }(),
    requestAllResultPageData: function requestAllResultPageData() {
      var _this2 = this;

      var isRequestTeamTop15 = this.var_isForceSubmit || this.var_teacherHasTakeUp || this.var_isPlayBack;

      set_timeout_default()(function () {
        isRequestTeamTop15 ? _this2.requestTeamTop15ListData() : promise_default.a.resolve();
      }, 0);

      return promise_default.a.all([this.requestStudentAnwseredResultData(), this.requestTeamAnsweredPieData(), isRequestTeamTop15 ? this.requestTeamTop15ListData() : promise_default.a.resolve()]);
    },
    // 获取学生作答情况，自身答案和正确答案统计列表
    requestStudentAnwseredResultData: function () {
      var _requestStudentAnwseredResultData = asyncToGenerator_default()(
      /*#__PURE__*/
      regenerator_default.a.mark(function _callee3() {
        var res;
        return regenerator_default.a.wrap(function _callee3$(_context4) {
          while (1) {
            switch (_context4.prev = _context4.next) {
              case 0:
                _context4.next = 2;
                return createApi.API_getStudentAnsweredResultData(formatParams);

              case 2:
                res = _context4.sent;
                console.log('学生作答结果和答案数据 ===>', res.data);
                this.var_studentAnsweredResultData = res.data;

              case 5:
              case "end":
                return _context4.stop();
            }
          }
        }, _callee3, this);
      }));

      function requestStudentAnwseredResultData() {
        return _requestStudentAnwseredResultData.apply(this, arguments);
      }

      return requestStudentAnwseredResultData;
    }(),
    // 获取组内作答的饼图统计接口
    requestTeamAnsweredPieData: function () {
      var _requestTeamAnsweredPieData = asyncToGenerator_default()(
      /*#__PURE__*/
      regenerator_default.a.mark(function _callee4() {
        var res;
        return regenerator_default.a.wrap(function _callee4$(_context5) {
          while (1) {
            switch (_context5.prev = _context5.next) {
              case 0:
                _context5.next = 2;
                return createApi.API_getTeamAnwseredResultPieData(formatParams);

              case 2:
                res = _context5.sent;
                console.log('组内答题情况饼图数据 ===>', res.data);
                this.initChartsForPie(res.data);

              case 5:
              case "end":
                return _context5.stop();
            }
          }
        }, _callee4, this);
      }));

      function requestTeamAnsweredPieData() {
        return _requestTeamAnsweredPieData.apply(this, arguments);
      }

      return requestTeamAnsweredPieData;
    }(),
    requestTeamTop15ListData: function () {
      var _requestTeamTop15ListData = asyncToGenerator_default()(
      /*#__PURE__*/
      regenerator_default.a.mark(function _callee5() {
        var res;
        return regenerator_default.a.wrap(function _callee5$(_context6) {
          while (1) {
            switch (_context6.prev = _context6.next) {
              case 0:
                _context6.next = 2;
                return createApi.API_getTeamTop15ListData(formatParams);

              case 2:
                res = _context6.sent;
                console.log('战队排名数据 ===>', res.data);
                this.var_teamTop15ListData = res.data;

              case 5:
              case "end":
                return _context6.stop();
            }
          }
        }, _callee5, this);
      }));

      function requestTeamTop15ListData() {
        return _requestTeamTop15ListData.apply(this, arguments);
      }

      return requestTeamTop15ListData;
    }(),
    initSwiper: function initSwiper() {
      var _this3 = this;

      return new promise_default.a(function (resolve) {
        set_timeout_default()(function () {
          swiper(_this3.$refs.swiperRef, 1214, function (index) {
            return _this3.var_currentPointIndex = index;
          });
          resolve();
        }, 30);
      });
    },
    initChartsForPie: function initChartsForPie(data) {
      var _ref = data || {},
          _ref$totalNum = _ref.totalNum,
          totalNum = _ref$totalNum === void 0 ? 1 : _ref$totalNum,
          _ref$charts = _ref.charts,
          charts = _ref$charts === void 0 ? [] : _ref$charts;

      var chartData = map_default()(charts).call(charts, function (item) {
        item.preset = item.value / totalNum;
        item.type = item.type * 1 === 0 ? 'wrong' : item.type * 1 === 1 ? 'partRight' : 'right';
        return item;
      });

      if (this.$refs.chartsRef) pie(this.$refs.chartsRef, chartData);
    },
    handleTeacherTackUp: function () {
      var _handleTeacherTackUp = asyncToGenerator_default()(
      /*#__PURE__*/
      regenerator_default.a.mark(function _callee6() {
        return regenerator_default.a.wrap(function _callee6$(_context7) {
          while (1) {
            switch (_context7.prev = _context7.next) {
              case 0:
                this.var_teacherHasTakeUp = true;
                window.location.hash = '#hasTakeUp';

                if (this.var_initReady) {
                  _context7.next = 4;
                  break;
                }

                return _context7.abrupt("return", this.requestAllResultPageData());

              case 4:
                _context7.next = 6;
                return loading_$loading({
                  message: '老师正在收卷，请稍后...',
                  requestList: [this.requestAllResultPageData(), Appvue_type_script_lang_js_sleep(1000)]
                });

              case 6:
              case "end":
                return _context7.stop();
            }
          }
        }, _callee6, this);
      }));

      function handleTeacherTackUp() {
        return _handleTeacherTackUp.apply(this, arguments);
      }

      return handleTeacherTackUp;
    }(),
    // 关闭结果页
    handleCloseWebPage: function handleCloseWebPage() {
      window.location.href = 'https://www.baidu.com';
    }
  }
});
// CONCATENATED MODULE: ./src/App.vue?vue&type=script&lang=js&
 /* harmony default export */ var src_Appvue_type_script_lang_js_ = (Appvue_type_script_lang_js_); 
// CONCATENATED MODULE: ./src/App.vue





/* normalize component */

var App_component = Object(componentNormalizer["a" /* default */])(
  src_Appvue_type_script_lang_js_,
  Appvue_type_template_id_02c6690d_render,
  staticRenderFns,
  false,
  null,
  null,
  null
  
)

/* harmony default export */ var App = (App_component.exports);
// EXTERNAL MODULE: ./src/styles/index.scss
var styles = __webpack_require__(166);

// CONCATENATED MODULE: ./src/index.js



vue_runtime_default.a.config.productionTip = false;
new vue_runtime_default.a({
  el: '#root',
  render: function render(h) {
    return h(App);
  }
});

/***/ }),

/***/ 31:
/***/ (function(module, exports, __webpack_require__) {

// extracted by mini-css-extract-plugin

/***/ })

},[[167,"manifest","vendors~main"]]]);
//# sourceMappingURL=main.97ef0a3ac5e0803bc33c.js.map