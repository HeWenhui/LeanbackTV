(window.webpackJsonp=window.webpackJsonp||[]).push([["bundle"],{135:function(t,e,s){s(136),t.exports=s(345)},340:function(t,e,s){t.exports=s.p+"assets/loading.gif?b11c27028f508c5526fc3fb65d97b6f3"},341:function(t,e,s){"use strict";var a=s(63);s.n(a).a},344:function(t,e,s){},345:function(t,e,s){"use strict";s.r(e);var a=s(50),r=s.n(a),n=s(49),i=s.n(n),c=window.location.search;console.log("当前 url query 参数:",i.a.parse(c));for(var o=i.a.parse(c),u=o.forceSubmit,l=o.stuId,v=o.stuCouId,d=o.packageId,h=o.liveId,f=o.packageSource,p=o.packageAttr,_=o.releasedPageInfos,m=o.isPlayBack,g=o.classId,w=o.classTestId,C=o.educationStage,T=o.teamId,k=o.nonce,x=o.chs,y=_&&JSON.parse(_)||[],R=[],P=[],b=0;b<y.length;b++){var D=y[b];for(var A in D)R.push(D[A][0]),P.push(D[A][1])}var I={forceSubmit:u,stuId:l,stuCouId:v,packageId:d,liveId:h,packageSource:f,packageAttr:p,releasedPageInfos:_,isPlayBack:m,classId:g,classTestId:w,srcTypes:R.join(","),testIds:P.join(","),educationStage:C,teamId:T,nonce:k,chs:x};console.log("解析完成的 query 参数:",I);var S=I,q=s(134),L=s.n(q).a.create({baseURL:"https://live.xueersi.com/science/Tutorship/",transformRequest:[function(t){return i.a.stringify(t)}]}),M=function(t){return new Promise(function(e,s){t.then(function(t){200===t.status?e(t.data):s(new Error("API 接口请求错误！ Code: "+t.status))}).catch(function(t){return s(t)})})},B=function(t){return M(L.get("/getStuTestResult",{params:t}))},$=function(t){return M(L.get("/teamStatistics",{params:t}))},E=function(t){return M(L.get("/top15List",{params:t}))},N=function(t){t.style.transition="transform 0.15s linear",t.style.webkitTransition="transform 0.15s linear"},U=function(t){t.style.transition="none",t.style.webkitTransition="none"},F=function(t,e){t.style.transform="translate3d(".concat(e,", 0, 0)"),t.style.webkitTransform="translate3d(".concat(e,", 0, 0)")};var H={wrong:{title:"全部错误",textAlign:"right",line:{start:[7.76,.7],end:[9.92,.7]}},partRight:{title:"部分正确",textAlign:"right",line:{start:[7.76,2],end:[9.92,2]}},right:{title:"全部正确",textAlign:"left",line:{start:[3.76,1.4],end:[1.6,1.4]}}},J={right:"#28B3AC",partRight:"#FFBB67",wrong:"#FF7878"},O=function(t,e,s,a,r){var n=.12*r,i=H[a].line.start[0]*r,c=H[a].line.start[1]*r;t.save(),t.fillStyle=e,t.arc(i,c,n,0,2*Math.PI),t.fill(),t.restore();var o=H[a].line.end[0]*r,u=H[a].line.end[1]*r,l=H[a].title,v="0%";v=1*s==1?"100%":1*s?Math.round(100*s)+"%":"0%",t.save(),t.strokeStyle=e,t.fillStyle=e,t.lineWidth=.04*r,t.textAlign=H[a].textAlign,t.font=.3*r+"px Arial",t.beginPath(),t.lineTo(i,c),t.lineTo(o,u),t.stroke(),t.fillText(v,o,u-.13*r),t.fillText(l,o,u+.35*r),t.restore()};var j={data:function(){return{visible:!1,message:"加载中，请稍后..."}},methods:{open:function(){this.visible=!0},close:function(){this.visible=!0,this.$el.parentNode.removeChild(this.$el),this.$destroy(!0)}}},W=(s(341),s(64)),G=Object(W.a)(j,function(){var t=this.$createElement,e=this._self._c||t;return e("div",{directives:[{name:"show",rawName:"v-show",value:this.visible,expression:"visible"}],staticClass:"loading"},[e("img",{staticClass:"img",attrs:{src:s(340)}}),this._v(" "),this._m(0),this._v(" "),e("div",{staticClass:"meta"},[this._v(this._s(this.message))])])},[function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"bar"},[e("div",{staticClass:"percent"})])}],!1,null,"bf230138",null).exports;function X(t,e,s,a,r,n,i){try{var c=t[n](i),o=c.value}catch(t){return void s(t)}c.done?e(o):Promise.resolve(o).then(a,r)}var Y=r.a.extend(G),z=function(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};return new Promise(function(){var e,s=(e=regeneratorRuntime.mark(function e(s){var a,r,n;return regeneratorRuntime.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return a=new Y({data:t}),r=t.requestList,n=void 0===r?[]:r,a.$mount(),document.body.appendChild(a.$el),a.open(),e.next=7,i=n,Promise.all(i);case 7:a.close(),s();case 9:case"end":return e.stop()}var i},e)}),function(){var t=this,s=arguments;return new Promise(function(a,r){var n=e.apply(t,s);function i(t){X(n,a,r,i,c,"next",t)}function c(t){X(n,a,r,i,c,"throw",t)}i(void 0)})});return function(t){return s.apply(this,arguments)}}())};function K(t,e,s,a,r,n,i){try{var c=t[n](i),o=c.value}catch(t){return void s(t)}c.done?e(o):Promise.resolve(o).then(a,r)}function Q(t){return function(){var e=this,s=arguments;return new Promise(function(a,r){var n=t.apply(e,s);function i(t){K(n,a,r,i,c,"next",t)}function c(t){K(n,a,r,i,c,"throw",t)}i(void 0)})}}var V=!!(1*S.forceSubmit),Z=!!(1*S.isPlayBack),tt=function(t){return new Promise(function(e){return setTimeout(e,t)})},et={data:function(){return{var_initReady:!1,var_isForceSubmit:V,var_isPlayBack:Z,var_teamAnsweredPieData:null,var_teamTop15ListData:{},var_studentAnsweredResultData:{},var_chartsData:null,var_currentPointIndex:0,var_requestTeamPieDataTimes:0,var_requestTeamTop15Times:0,var_maxRequestTimes:15,var_teacherHasTakeUp:!1}},computed:{getter_isShowResultPage:function(){return this.var_studentAnsweredResultData&&this.var_teamAnsweredPieData},getter_resutlPageTypeData:function(){var t=this.var_studentAnsweredResultData,e=t.gold,s="",a="";switch(t.type){case 0:s="w",a="很遗憾答错了，没有获得金币哟！";break;case 1:s="r",a="恭喜你答对了，获得".concat(e,"个金币！");break;case 2:s="p",a="部分正确，获得".concat(e,"个金币！")}return{backgroundClass:s,resultText:a}},getter_resultBackgroundClass:function(){return this.getter_resutlPageTypeData.backgroundClass},getter_resultPageText:function(){return this.getter_resutlPageTypeData.resultText},getter_studentAnswerList:function(){return this.var_studentAnsweredResultData.answerLists||[]},getter_teamGroupTop15Data:function(){return this.var_teamTop15ListData?this.var_teamTop15ListData.teamRank:[]},getter_teamClassTop15Data:function(){return this.var_teamTop15ListData?this.var_teamTop15ListData.classRank:[]}},created:function(){window._fn_({fontSize:100,width:1334})},mounted:function(){var t=Q(regeneratorRuntime.mark(function t(){return regeneratorRuntime.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return window.__CLIENT_SUBMIT__=this.handleTeacherTackUp.bind(this),"#hasTakeUp"===window.location.hash&&(this.var_teacherHasTakeUp=!0),t.next=4,this.initResultPage();case 4:e={methodName:"resultPageLoaded"},window.webkit&&window.webkit.messageHandlers.xesApp?window.webkit.messageHandlers.xesApp.postMessage(JSON.stringify(e)):window.xesApp&&window.xesApp[e.methodName](JSON.stringify(e));case 5:case"end":return t.stop()}var e},t,this)}));return function(){return t.apply(this,arguments)}}(),methods:{initResultPage:function(){var t=Q(regeneratorRuntime.mark(function t(){var e=this;return regeneratorRuntime.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,this.initSwiper();case 2:return t.next=4,z({requestList:[this.requestAllResultPageData()]});case 4:this.var_initReady=!0,setInterval(function(){++e.var_requestTeamPieDataTimes<e.var_maxRequestTimes&&e.requestTeamAnsweredPieData(),(e.var_isForceSubmit||e.var_teacherHasTakeUp)&&++e.var_requestTeamTop15Times<e.var_maxRequestTimes&&e.requestStudentAnwseredResultData()},8e3);case 6:case"end":return t.stop()}},t,this)}));return function(){return t.apply(this,arguments)}}(),requestAllResultPageData:function(){var t=this.var_isForceSubmit||this.var_teacherHasTakeUp||this.var_isPlayBack;return Promise.all([this.requestStudentAnwseredResultData(),this.requestTeamAnsweredPieData(),t?this.requestTeamTop15ListData():Promise.resolve()])},requestStudentAnwseredResultData:function(){var t=Q(regeneratorRuntime.mark(function t(){var e;return regeneratorRuntime.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,B(S);case 2:e=t.sent,console.log("学生作答结果和答案数据 ===>",e.data),this.var_studentAnsweredResultData=e.data;case 5:case"end":return t.stop()}},t,this)}));return function(){return t.apply(this,arguments)}}(),requestTeamAnsweredPieData:function(){var t=Q(regeneratorRuntime.mark(function t(){var e;return regeneratorRuntime.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,$(S);case 2:e=t.sent,console.log("组内答题情况饼图数据 ===>",e.data),this.initChartsForPie(e.data);case 5:case"end":return t.stop()}},t,this)}));return function(){return t.apply(this,arguments)}}(),requestTeamTop15ListData:function(){var t=Q(regeneratorRuntime.mark(function t(){var e;return regeneratorRuntime.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,E(S);case 2:e=t.sent,console.log("战队排名数据 ===>",e.data),this.var_teamTop15ListData=e.data;case 5:case"end":return t.stop()}},t,this)}));return function(){return t.apply(this,arguments)}}(),initSwiper:function(){var t=this;return new Promise(function(e){setTimeout(function(){var s,a,r,n,i,c,o,u,l,v,d,h;s=t.$refs.swiperRef,a=1214,r=function(e){return t.var_currentPointIndex=e},n=s.children[0],i=n.children.length,c=0,o=0,l=!1,v=0,d=0,h=0,F(n,-(u=0)*a/100+"rem"),s.addEventListener("touchstart",function(t){c=t.touches[0].pageX,o=t.touches[0].pageY,h=Date.now()}),s.addEventListener("touchmove",function(t){var e=t.touches[0],s=e.pageX,r=e.pageY;if(v=s-c,d=r-o,Math.abs(v)-Math.abs(d)<10)return!1;t.preventDefault(),U(n);var h=-u*a+v;v>0&&0===u&&(h=0),v<0&&u===i-1&&(h=(i-1)*-a),F(n,h/100+"rem"),l=!0}),s.addEventListener("touchend",function(){l&&Math.abs(v)>a/6&&Date.now()-h>50&&(v>0&&u--,v<0&&u++,u=Math.max(0,u),u=Math.min(u,i-1)),N(n),F(n,-u*a/100+"rem"),r&&r(u),c=o=v=d=0,l=!1}),e()},30)})},initChartsForPie:function(t){var e=t||{},s=e.totalNum,a=void 0===s?1:s,r=e.charts,n=(void 0===r?[]:r).map(function(t){return t.preset=t.value/a,t.type=1*t.type==0?"wrong":1*t.type==1?"partRight":"right",t});this.$refs.chartsRef&&function(t,e){var s=t.children[0],a=s.getContext("2d"),r=document.documentElement.clientWidth/1334*100*2,n=s.width=11.94*r,i=s.height=3*r,c=n/2,o=i/2,u=.6*r,l=1.38*r,v=3*Math.PI/2,d=v,h="#fff";a.clearRect(0,0,n,i);for(var f=0;f<e.length;f++)d=v+2*Math.PI*e[f].preset,h=J[e[f].type],a.save(),a.fillStyle=h,a.beginPath(),a.arc(c,o,l,d,v,!0),a.arc(c,o,u,v,d),a.closePath(),a.fill(),a.restore(),v=d,O(a,h,e[f].preset,e[f].type,r)}(this.$refs.chartsRef,n)},handleTeacherTackUp:function(){var t=Q(regeneratorRuntime.mark(function t(){return regeneratorRuntime.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:if(this.var_teacherHasTakeUp=!0,window.location.hash="#hasTakeUp",this.var_initReady){t.next=4;break}return t.abrupt("return",this.requestAllResultPageData());case 4:return t.next=6,z({message:"老师正在收卷，请稍后...",requestList:[this.requestAllResultPageData(),tt(1e3)]});case 6:case"end":return t.stop()}},t,this)}));return function(){return t.apply(this,arguments)}}(),handleCloseWebPage:function(){window.location.href="https://www.baidu.com"}}},st=Object(W.a)(et,function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{class:["result-root",t.getter_resultBackgroundClass]},[s("div",{staticClass:"result-info"},[t._v(t._s(t.getter_resultPageText))]),t._v(" "),s("div",{staticClass:"close",on:{click:t.handleCloseWebPage}}),t._v(" "),s("div",{ref:"swiperRef",staticClass:"swiper"},[s("div",{staticClass:"swiper-list-group"},[s("div",{staticClass:"swiper-item"},[s("div",{staticClass:"answer-info"},[t._m(0),t._v(" "),s("div",{staticClass:"content"},t._l(t.getter_studentAnswerList,function(e,a){return s("div",{key:e.testId,staticClass:"answer-item"},[s("div",{staticClass:"i index"},[t._v(t._s(a+1))]),t._v(" "),s("div",{staticClass:"i expect"},[s("div",{staticClass:"answer-wrap"},t._l(e.rightAnswer,function(e){return s("div",{key:e},[t._v(t._s(e))])}),0)]),t._v(" "),s("div",{staticClass:"i yours"},[s("div",{staticClass:"answer-wrap"},t._l(e.stuAnswer,function(e,a){return s("div",{key:a,class:{r:1*e.right==1,w:1*e.right==0,sf:!e.answer}},[t._v("\n                    "+t._s(e.answer?e.answer:"未作答")+"\n                  ")])}),0)]),t._v(" "),s("div",{staticClass:"i result",class:{r:1*e.isRight==2,p:1*e.isRight==1,w:1*e.isRight==0}})])}),0)])]),t._v(" "),s("div",{staticClass:"swiper-item"},[s("div",{staticClass:"pie-info"},[s("div",{staticClass:"header"},[s("span",{staticClass:"icon",class:t.getter_resultBackgroundClass}),t._v("\n            组内答题情况\n          ")]),t._v(" "),s("div",{ref:"chartsRef",staticClass:"pie"},[s("canvas",{attrs:{id:"canvas"}})])])]),t._v(" "),s("div",{staticClass:"swiper-item",staticStyle:{"background-color":"rgba(0, 0, 0, 0)"}},[s("div",{staticClass:"top-info"},[s("div",{staticClass:"top-15 left",class:t.getter_resultBackgroundClass},[s("div",{staticClass:"header"},[t._v("组内前15名")]),t._v(" "),s("div",{staticClass:"list"},[t._m(1),t._v(" "),s("div",{staticClass:"list-content"},t._l(t.getter_teamGroupTop15Data,function(e,a){return s("div",{key:e.stuId,staticClass:"item"},[s("div",{staticClass:"index",class:{one:0===a,two:1===a,three:2===a}},[t._v("\n                  "+t._s(a>2?a+1:"")+"\n                  ")]),t._v(" "),s("div",{staticClass:"name",class:{own:1*e.isMe==1}},[t._v(t._s(e.stuName))]),t._v(" "),s("div",{staticClass:"status",class:{own:1*e.isMe==1}},[t._v(t._s(e.correctRate))])])}),0)])]),t._v(" "),s("div",{staticClass:"top-15 right",class:t.getter_resultBackgroundClass},[s("div",{staticClass:"header"},[t._v("班内前15名")]),t._v(" "),s("div",{staticClass:"list"},[t._m(2),t._v(" "),s("div",{staticClass:"list-content"},t._l(t.getter_teamClassTop15Data,function(e,a){return s("div",{key:e.stuId,staticClass:"item"},[s("div",{staticClass:"index",class:{one:0===a,two:1===a,three:2===a}},[t._v("\n                  "+t._s(a>2?a+1:"")+"\n                  ")]),t._v(" "),s("div",{staticClass:"name",class:{own:1*e.isMe==1}},[t._v(t._s(e.stuName))]),t._v(" "),s("div",{staticClass:"status",class:{own:1*e.isMe==1}},[t._v(t._s(e.correctRate))])])}),0)])])])])])]),t._v(" "),s("div",{staticClass:"swiper-pointer-group"},t._l(3,function(e){return s("span",{key:e,staticClass:"pointer",class:{active:t.var_currentPointIndex===e-1}})}),0)])},[function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"header"},[e("div",{staticClass:"i index"},[this._v("题号")]),this._v(" "),e("div",{staticClass:"i expect"},[this._v("正确答案")]),this._v(" "),e("div",{staticClass:"i yours"},[this._v("你的答案")]),this._v(" "),e("div",{staticClass:"i result"},[this._v("答案情况")])])},function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"list-header"},[e("div",{staticClass:"index"},[this._v("排名")]),this._v(" "),e("div",{staticClass:"name"},[this._v("姓名")]),this._v(" "),e("div",{staticClass:"status"},[this._v("正确率")])])},function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"list-header"},[e("div",{staticClass:"index"},[this._v("排名")]),this._v(" "),e("div",{staticClass:"name"},[this._v("姓名")]),this._v(" "),e("div",{staticClass:"status"},[this._v("正确率")])])}],!1,null,null,null).exports;s(344);r.a.config.productionTip=!1,new r.a({el:"#root",render:function(t){return t(st)}})},63:function(t,e,s){}},[[135,"manifest","vendors~bundle"]]]);