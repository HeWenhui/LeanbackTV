(window.webpackJsonp=window.webpackJsonp||[]).push([["bundle"],{139:function(t,s,e){e(140),t.exports=e(351)},326:function(t,s,e){"use strict";var i=e(63);e.n(i).a},327:function(t,s,e){"use strict";var i=e(64);e.n(i).a},346:function(t,s,e){"use strict";var i=e(65);e.n(i).a},349:function(t,s,e){"use strict";var i=e(66);e.n(i).a},350:function(t,s,e){"use strict";var i=e(67);e.n(i).a},351:function(t,s,e){"use strict";e.r(s);var i=e(40),a=e.n(i),n=e(68),l=e.n(n),r={methods:{dispatch:function(t,s){for(var e=this.$parent||this.$root,i=e.$options.name;e&&(!i||t!==i);)(e=e.$parent)&&(i=e.$options.name);for(var a=arguments.length,n=new Array(a>2?a-2:0),l=2;l<a;l++)n[l-2]=arguments[l];e&&e.$emit.apply(e,[s].concat(n))},broadcoast:function(t,s){for(var e=arguments.length,i=new Array(e>2?e-2:0),a=2;a<e;a++)i[a-2]=arguments[a];(function t(s,e){for(var i=this,a=arguments.length,n=new Array(a>2?a-2:0),l=2;l<a;l++)n[l-2]=arguments[l];this.$children.forEach(function(a){a.$options.name===s?a.$emit.apply(a,[e].concat(n)):t.apply(i,[s,e].concat(n))})}).apply(this,[t,s].concat(i))}}},o=function(t,s){t.style.transform="translate3d(".concat(s,"rem, 0, 0)"),t.style.webkitTransform="translate3d(".concat(s,"rem, 0, 0)")},c={name:"slider",props:{itemSize:{type:Number,default:12.14},offset:{type:Number,default:.64},defaultIndex:{type:Number,default:1}},data:function(){return{sliders:[],index:this.defaultIndex,startX:0,startY:0,startTime:0,dx:0,dy:0}},mixins:[r],created:function(){var t=this;this.$on("add",function(s){return t.sliders.push(s)})},computed:{sliderNum:function(){return this.sliders.length},wrapSize:function(){return this.sliderNum*this.itemSize+"rem"}},mounted:function(){o(this.$refs.wrapper,-this.defaultIndex*this.itemSize)},methods:{touchStartFn:function(t){this.startX=t.touches[0].pageX,this.startY=t.touches[0].pageY,this.startTime=Date.now(),this.$emit("start")},touchMoveFn:function(t){if(this.$emit("move"),this.dx=t.touches[0].pageX-this.startX,this.dy=t.touches[0].pageY-this.startY,Math.abs(this.dx)-Math.abs(this.dy)<50)return!1;t.preventDefault();var s,e=this.$refs.wrapper;(s=e).style.transition="none",s.style.webkitTransition="none";var i=this.dx/100-this.index*this.itemSize;this.dx>0&&0===this.index&&(i=0),this.dx<0&&this.index===this.sliderNum-1&&(i=(this.sliderNum-1)*-this.itemSize),o(e,i)},touchEndFn:function(t){var s;Math.abs(this.dx)-Math.abs(this.dy)>50&&Math.abs(this.dx/100)>this.itemSize/6&&Date.now()-this.startTime>50&&(this.dx>0&&this.index--,this.dx<0&&this.index++,this.index=Math.max(0,this.index),this.index=Math.min(this.index,this.sliderNum-1)),(s=this.$refs.wrapper).style.transition="transform 0.15s linear",s.style.webkitTransition="transform 0.15s linear",o(this.$refs.wrapper,-this.index*this.itemSize),this.startX=this.startY=this.dx=this.dy=0,this.$emit("change",this.index)}}},d=(e(326),e(24)),u=Object(d.a)(c,function(){var t=this,s=t.$createElement,e=t._self._c||s;return e("div",{staticClass:"slider-box"},[e("div",{ref:"wrapper",staticClass:"slider-warpper",style:{width:t.wrapSize,left:t.offset+"rem"},on:{touchstart:t.touchStartFn,touchmove:t.touchMoveFn,touchend:t.touchEndFn}},[t._t("default")],2),t._v(" "),e("div",{staticClass:"points"},t._l(t.sliderNum,function(s,i){return e("span",{key:i,staticClass:"item",class:{active:t.index===i}})}),0)])},[],!1,null,null,null).exports,v={name:"sliderItem",props:{color:{type:String,default:"#ffffff"}},data:function(){return{}},mixins:[r],created:function(){this.dispatch("slider","add",this)}},h=(e(327),Object(d.a)(v,function(){var t=this.$createElement;return(this._self._c||t)("div",{staticClass:"slider-item-box",style:{backgroundColor:this.color}},[this._t("default")],2)},[],!1,null,null,null).exports),f=e(138),m=e.n(f).a.create({baseURL:"https://live.xueersi.com"}),_=window.location.search,p=l.a.parse(_),C=function(t){return function(t){return new Promise(function(s,e){t.then(function(t){s(t.data)}).catch(function(t){return e(t)})})}(m.get("/science/LiveExam/getResultStatistic",{params:t}))},x={data:function(){return{text:"",visible:!1}},methods:{close:function(){this.visible=!1},handleCloseText:function(t){this.close()}}},w=(e(346),Object(d.a)(x,function(){var t=this.$createElement,s=this._self._c||t;return s("transition",{attrs:{name:"fade"}},[s("div",{directives:[{name:"show",rawName:"v-show",value:this.visible,expression:"visible"}],staticClass:"text-box",on:{touchend:this.handleCloseText}},[s("div",{staticClass:"text"},[this._v(this._s(this.text))])])])},[],!1,null,null,null).exports),b=a.a.extend(w),y=function(t){window.webkit&&window.webkit.messageHandlers.xesApp?window.webkit.messageHandlers.xesApp.postMessage(JSON.stringify(t)):window.xesApp&&window.xesApp[t.methodName](JSON.stringify(t))},g={data:function(){return{visible:!1,scale:1,resultData:{}}},methods:{closeModal:function(){this.visible=!1}}},k=(e(349),Object(d.a)(g,function(){var t=this,s=t.$createElement,e=t._self._c||s;return e("transition",{attrs:{name:"fade"}},[e("div",{directives:[{name:"show",rawName:"v-show",value:t.visible,expression:"visible"}],staticClass:"modal",on:{touchend:t.closeModal}},[e("div",{staticClass:"wrap",style:{transform:"scale("+t.scale+")"}},[e("div",{staticClass:"img"}),t._v(" "),e("ul",{staticClass:"list"},[e("li",{staticClass:"item"},[e("div",[t._v("做对步骤")]),t._v(" "),e("div",[t._v(t._s(t.resultData.rightStep)+"/"+t._s(t.resultData.totalStep))])]),t._v(" "),e("li",{staticClass:"item"},[e("div",[t._v("获得金币")]),t._v(" "),e("div",{staticClass:"num-box"},[e("div",{staticClass:"num"},[t._v("+"+t._s(t.resultData.gold))])])]),t._v(" "),t.resultData.reward?e("li",{staticClass:"item"},[e("div",[t._v("连对N次")]),t._v(" "),e("div",{staticClass:"num-box"},[e("div",{staticClass:"num"},[t._v("+"+t._s(t.resultData.reward))])])]):t._e()])])])])},[],!1,null,null,null).exports),T=a.a.extend(k),$=function(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},s=new T({data:t});s.$mount(),document.body.appendChild(s.$el),s.visible=!0,setTimeout(function(){s.visible=!1},3e3)};function M(t,s,e,i,a,n,l){try{var r=t[n](l),o=r.value}catch(t){return void e(t)}r.done?s(o):Promise.resolve(o).then(i,a)}l.a.parse(window.location.search);var S=window.location.hash,D=S.indexOf("tackup")>-1||1*p.force==1,I=S.indexOf("modal")<0,N=1*p.isPlayBack==1,F={components:{slider:u,sliderItem:h},data:function(){return{resultData:{score:{},comment:[],detals:[],teamList:[],classList:[]},hasTackUp:D,titleList:["实验结果","得分细则","实验点评","班级排名"],titleIndex:0,ifCanIntoTestMode:1*p.testMode==1,isPlayBack:N}},mounted:function(){var t,s=(t=regeneratorRuntime.mark(function t(){var s,e=this;return regeneratorRuntime.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return window.__fn__(),t.next=3,C(p);case 3:s=t.sent,console.log(s.data),this.resultData=s.data,I&&($({resultData:this.resultData}),window.location.hash+="-modal=1"),window.onTeachTakeUp=function(){e.hasTackUp||(e.hasTackUp=!0,window.location.hash+="-tackup=1")};case 8:case"end":return t.stop()}},t,this)}),function(){var s=this,e=arguments;return new Promise(function(i,a){var n=t.apply(s,e);function l(t){M(n,i,a,l,r,"next",t)}function r(t){M(n,i,a,l,r,"throw",t)}l(void 0)})});return function(){return s.apply(this,arguments)}}(),methods:{showStepDetails:function(t){this.isSliderMove||(this.textAlert=function(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},s=new b({data:t});return s.$mount(),document.body.appendChild(s.$el),s.visible=!0,setTimeout(function(){return s.visible=!1},3e3),s}({text:t}))},intoTestModelFn:function(){this.ifCanIntoTestMode&&!this.hasTackUp&&y({methodName:"intoTestMode"})},closeBackFn:function(){y({methodName:"togglePackUp"})},handleChangeTitle:function(t){this.titleIndex=t,this.isSliderMove=!1},handleMove:function(){this.isSliderMove=!0}}},U=(e(350),Object(d.a)(F,function(){var t=this,s=t.$createElement,e=t._self._c||s;return e("div",{staticClass:"full flex center",attrs:{id:"root"}},[e("div",{staticClass:"main-box cover"},[e("div",{staticClass:"title-box"},[t._v(t._s(t.titleList[t.titleIndex]))]),t._v(" "),e("div",{staticClass:"test-link",class:{disable:!t.ifCanIntoTestMode||t.hasTackUp},on:{touchstart:t.intoTestModelFn}},[t._v("进入练习模式 >")]),t._v(" "),e("div",{staticClass:"back-btn",on:{touchstart:t.closeBackFn}},[t._v("收起实验报告")]),t._v(" "),e("slider",{attrs:{defaultIndex:0},on:{change:t.handleChangeTitle,move:t.handleMove}},[e("sliderItem",[e("div",{staticClass:"scores-box full"},[e("div",{staticClass:"score-box fw flex center"},[e("span",{staticClass:"num"},[t._v(t._s(t.resultData.score.num))]),t._v("分")]),t._v(" "),t.isPlayBack?t._e():e("div",{staticClass:"pass-box fw flex center"},[t._v("超过全班"+t._s(Math.round(100*t.resultData.score.percent))+"%同学")]),t._v(" "),e("div",{staticClass:"gold-box cover"},[e("span",{staticClass:"num"},[t._v(t._s(t.resultData.gold))])]),t._v(" "),e("div",{staticClass:"reward-box"},[t._v("连对奖励："+t._s(t.resultData.reward)+"金币")])])]),t._v(" "),e("sliderItem",[e("div",{staticClass:"details-box full"},[e("div",{staticClass:"table full flex column"},[e("div",{staticClass:"header tr"},[e("div",{staticClass:"td"},[t._v("步骤")]),t._v(" "),e("div",{staticClass:"td"},[e("div",{staticClass:"item"},[t._v("步骤详情")])]),t._v(" "),e("div",{staticClass:"td"},[e("div",{staticClass:"item"},[t._v("得分")])]),t._v(" "),e("div",{staticClass:"td"},[e("div",{staticClass:"item"},[t._v("操作结果")])])]),t._v(" "),e("div",{staticClass:"body auto"},t._l(t.resultData.detals,function(s,i){return e("div",{key:s.name,staticClass:"tr"},[e("div",{staticClass:"td"},[e("span",[t._v(t._s(i+1)+" "+t._s(s.name))])]),t._v(" "),e("div",{staticClass:"td"},t._l(s.subStep,function(s,a){return e("div",{key:a,staticClass:"item",on:{touchend:function(e){return t.showStepDetails(s.name)}}},[t._v("\n                    "+t._s(i+1+"-"+(a+1))+" "+t._s(s.name)+"\n                  ")])}),0),t._v(" "),e("div",{staticClass:"td"},t._l(s.subStep,function(s,i){return e("div",{key:i,staticClass:"item"},[t._v(t._s(s.score))])}),0),t._v(" "),e("div",{staticClass:"td"},t._l(s.subStep,function(s,i){return e("div",{key:i,staticClass:"item"},[t._v(t._s(s.right))])}),0)])}),0)])])]),t._v(" "),e("sliderItem",[e("div",{staticClass:"commet-box full flex column"},[e("div",{staticClass:"header"},[t._v("在操作及数据处理上，需要注意以下几点：")]),t._v(" "),e("div",{staticClass:"body auto"},t._l(t.resultData.comment,function(s,i){return e("div",{key:i,staticClass:"item"},[t._v(t._s(i+1)+" "+t._s(s))])}),0)])]),t._v(" "),t.isPlayBack?t._e():e("sliderItem",{attrs:{color:"rgba(0, 0, 0, 0)"}},[e("div",{staticClass:"full flex between"},[e("div",{staticClass:"top-list flex column"},[e("div",{staticClass:"title"},[t._v("组内前15名")]),t._v(" "),e("div",{staticClass:"content flex column auto"},[e("div",{staticClass:"header tr"},[e("div",{staticClass:"td"},[t._v("排名")]),t._v(" "),e("div",{staticClass:"td"},[t._v("姓名")]),t._v(" "),e("div",{staticClass:"td"},[t._v("分数")])]),t._v(" "),e("div",{staticClass:"body auto"},t._l(t.resultData.teamList,function(s,i){return e("div",{key:s.stuId,staticClass:"tr"},[e("div",{staticClass:"td",class:{one:0==i,two:1==i,three:2==i}},[t._v(t._s(i<3?"":i+1))]),t._v(" "),e("div",{staticClass:"td"},[t._v(t._s(s.name))]),t._v(" "),e("div",{staticClass:"td"},[t._v(t._s(s.rightRate))])])}),0)])]),t._v(" "),e("div",{staticClass:"top-list flex column"},[e("div",{staticClass:"title"},[t._v("班级前15名")]),t._v(" "),e("div",{staticClass:"content flex column auto"},[t.hasTackUp?t._e():e("div",{staticClass:"no-data"},[t._v("老师收卷后出结果")]),t._v(" "),e("div",{staticClass:"header tr"},[e("div",{staticClass:"td"},[t._v("排名")]),t._v(" "),e("div",{staticClass:"td"},[t._v("姓名")]),t._v(" "),e("div",{staticClass:"td"},[t._v("分数")])]),t._v(" "),e("div",{staticClass:"body auto"},t._l(t.hasTackUp?t.resultData.classList:[],function(s,i){return e("div",{key:s.stuId,staticClass:"tr"},[e("div",{staticClass:"td",class:{one:0==i,two:1==i,three:2==i}},[t._v(t._s(i<3?"":i+1))]),t._v(" "),e("div",{staticClass:"td"},[t._v(t._s(s.name))]),t._v(" "),e("div",{staticClass:"td"},[t._v(t._s(s.rightRate))])])}),0)])])])])],1)],1)])},[],!1,null,null,null).exports);a.a.config.productionTip=!1,new a.a({el:"#root",render:function(t){return t(U)}})},63:function(t,s,e){},64:function(t,s,e){},65:function(t,s,e){},66:function(t,s,e){},67:function(t,s,e){}},[[139,"manifest","vendors~bundle"]]]);