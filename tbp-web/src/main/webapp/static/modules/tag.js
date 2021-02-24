/**

 @Name：layui.age Tag标签
 @Author：lih
 @License：MIT
    
 */
 
layui.define('jquery', function(exports){
  "use strict";
  
  var $ = layui.$
  ,MOD_NAME = 'tag',
  TAG_CLASS = '.tag',
  BUTTON_NEW_TAG ='button-new-tag',
  INPUT_NEW_TAG ='input-new-tag',
  TAG_ITEM ='tag-item',
  CLOSE = 'tag-close',
  DEFAULT_SKIN ='layui-btn layui-btn-primary layui-btn-sm'
  ,tag = function(){
    this.config = {
      likeHref:'../../modules/tag.css',
      skin: DEFAULT_SKIN,
      tagText:'+ New Tag'
    };
    this.configs = {}
  };
  
  //全局设置
  tag.prototype.set = function(options){
    var that = this;
    $.extend(true, that.config, options);
    tag.render();
    return that;
  };
  
  //表单事件监听
  tag.prototype.on = function(events, callback){
    return layui.onevent.call(this, MOD_NAME, events, callback);
  };
  
  //外部Tag新增
  tag.prototype.add = function(filter, options){
    var tagElem = $(TAG_CLASS + '[lay-filter='+ filter +']')
    call.add(null, tagElem, options);
    call.tagAuto(filter);
    return this;
  };
  
  //外部Tag删除
  tag.prototype.delete = function(filter, layid){
    var tagElem = $(TAG_CLASS + '[lay-filter='+ filter +']')
    ,tagItemElem = tagElem.find('>.' + TAG_ITEM + '[lay-id="'+ layid +'"]');
    call.delete(null, tagItemElem);
    return this;
  };

  // tag数据获取
  tag.prototype.data = function (filter) {
    var tagElem = $(TAG_CLASS + '[lay-filter=' + filter + ']');
    var data=[];
    tagElem.find(".tag-item").each(function (i,item) {
        var val= $(item).contents().filter(function (index, content) {
            return content.nodeType === 3;
        }).text();
        data.push(val);
    });
    return data;
  };  

  //基础事件体
  var call = {
    //Tag点击
    tagClick: function(e, index, tagItemElem, options){
      options = options || {};
      var othis = tagItemElem || $(this)
      ,index = index || othis.index(othis)
      ,parents = othis.parents(TAG_CLASS).eq(0)
      ,filter = parents.attr('lay-filter');
      layui.event.call(this, MOD_NAME, 'click('+ filter +')', {
        elem: parents
        ,index: index
      });
    }
    //Tag新增事件
    ,add: function(e, tagElem, options){
      var  filter = tagElem.attr('lay-filter'),
           buttonNewTag = tagElem.children('.' + BUTTON_NEW_TAG),
           index = buttonNewTag.index()
          ,newTag = '<button lay-id="'+ (options.id||'') +'"' +(options.attr ? ' lay-attr="'+ options.attr +'"' : '') +' type="button" class="' + TAG_ITEM  + '">'+ (options.text||'unnaming') +'</button>';
      var result = layui.event.call(this, MOD_NAME, 'add('+ filter +')', {
        elem: tagElem
        ,index: index
        ,othis: newTag
        ,value: options.text
      })
      if(result === false) return;
      buttonNewTag[0] ? buttonNewTag.before(newTag) : tagElem.append(newTag);
    }
    //Tag输入事件
    ,input: function(e, othis){
      var buttonNewTag = othis || $(this)
      ,parents = buttonNewTag.parents(TAG_CLASS).eq(0)
      ,filter = parents.attr('lay-filter')
      var options = tag.configs[filter] = $.extend({}, tag.config, tag.configs[filter] || {}, options);
      //标签输入框
      var inpatNewTag = $('<div class="' + INPUT_NEW_TAG + '"><input type="text" autocomplete="off" class="layui-input"></div>');
      inpatNewTag.addClass(options.skin);
      buttonNewTag.after(inpatNewTag).remove();

      var addFunction = function () {
        var value = inpatNewTag.children('.layui-input').val();
        if(value){
          var options = {
            text: value
          }
          call.add(null, parents, options);
        }
        inpatNewTag.remove();
        call.tagAuto(filter);
      };
      // 初始化事件
      inpatNewTag.children('.layui-input')
      // 鼠标移出
      .on('blur', addFunction)
      // 回车键
      .keypress(function(event){
        if (event.keyCode == 13) {
          addFunction();
          // 添加完成后，将光标移动到新增tag
          parents.children('.' + BUTTON_NEW_TAG).click();
        }
      })
      .focus();
    }
    //Tag删除
    ,delete: function(e, othis){
      var tagItem = othis || $(this).parent(), index = tagItem.index()
      ,parents = tagItem.parents(TAG_CLASS).eq(0)
      ,filter = parents.attr('lay-filter');

      var value= $(tagItem[0]).contents().filter(function (index, content) {
        return content.nodeType === 3;
      }).text()
      var result = layui.event.call(this, MOD_NAME, 'delete('+ filter +')', {
        elem: parents
        ,index: index
        ,value: value
      })
      if(result === false) return;
      tagItem.remove()
    }
    //Tag 自适应
    ,tagAuto: function(filter){
      filter = filter || '';
      var options = filter ? tag.configs[filter] || tag.config : tag.config;
      var elemFilter = function(){
        return filter ? ('[lay-filter="' + filter +'"]') : '';
      }();
      $(TAG_CLASS + elemFilter).each(function(){
        var othis = $(this),tagItem = othis.children('.' + TAG_ITEM), buttonNewTag = othis.children('.' + BUTTON_NEW_TAG);
        tagItem.removeClass(DEFAULT_SKIN).addClass(options.skin);
        //允许关闭
        if(othis.attr('lay-allowClose') && tagItem.length){
          tagItem.each(function(){
            var li = $(this);
            if(!li.find('.'+CLOSE)[0]){
              var close = $('<i class="layui-icon layui-unselect '+ CLOSE +'">&#x1006;</i>');
              close.on('click', call.delete);
              li.append(close);
            }
          });
        }
        //允许新增标签
        if(othis.attr('lay-newTag') && buttonNewTag.length === 0){
          buttonNewTag = $('<button type="button" class="' + BUTTON_NEW_TAG + '"></button>');
          buttonNewTag.on('click', call.input);
          othis.append(buttonNewTag);
        }
        buttonNewTag.html(options.tagText);
        buttonNewTag.removeClass(DEFAULT_SKIN).addClass(options.skin);
      });
    }
  };
  
  //初始化元素操作
  tag.prototype.init = function(filter, options){
    layui.addcss(tag.config.likeHref);
    if(filter){
      tag.configs[filter] = $.extend({}, tag.config, tag.configs[filter] || {}, options);
    }
    return call.tagAuto.call(this, filter);
  };
  
  tag.prototype.render = tag.prototype.init;

  var tag = new tag(), dom = $(document);
  tag.render();

  dom.on('click', '.' + TAG_ITEM, call.tagClick); //tag 单击事件
  exports(MOD_NAME, tag);
});

