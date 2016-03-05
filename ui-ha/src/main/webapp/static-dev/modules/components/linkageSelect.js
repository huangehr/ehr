/*!
 * linkageSelect -v1.0.0 (2015/8/13)
 * 
 * @description:
 *
 *      联动下拉框
 *
 * @example:
 *
 *    html代码：
        <select id="select1"></select>
        <select id="select2"></select>
        <select id="select3"></select>
 *    js代码:
      new LinkageSelect([
            {
                 selector:"#select1", // 一级联动下拉框id选择器
                 url: "${contextRoot}/user/getProvinces", // 一级联动下拉框数据请求路径
                 data: {level: 1}, // 一级联动下拉框数据请求发送给服务器的ajax数据
                 blankItem: true, // 是否插入置空选项行，默认为true
                 selected: { // 设置默认选择项
                    id: 3  // 根据服务器所返回的数据的id，设置默认选项
                 },
                 paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的value值和其显示文本设置为pid和province请求参数
                     id: 'pid',
                     name: 'province'
                 }
             },
             {
                 selector:"#select2", // 二级联动下拉框id选择器
                 url: "${contextRoot}/user/getCitys",
                 blankItem: true, // 是否插入置空选项行，默认为true
                 selected: {  // 设置默认选择项
                    index: 1 // 根据服务器所返数据的顺序索引，设置默认选项
                 },
                 paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    name: "city"
                 }
             },
             {
                 selector:"#select3", // 三级联动下拉框id选择器
                 url: "${contextRoot}/user/getOrgs"
             }
         ]);
 *
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */

define(function (require, exports, module) {

    var $ = require('jquery'),
        juicer    = require('juicer'),
        Util      = require('utils'),
        Component = require('component'),
        ViewController = require('viewController'),
        DModel      = require('dataModel').create();

    var tpl = '{@if blankItem===true}' +
                '<option value="" data-name=""></option>' +
              '{@/if}' +
              '{@each data as d,index}' +
                '<option value="{$$d.id}" data-name="{$$d.name}" {@if selected.index==(index+1) || selected.id== d.id} selected {@/if}>{$$d.name}</option>' +
              '{@/each}';
    /*
    * 为下拉框增加设值方法
    */
    function extend (select) {

        $.extend(select,{
            setValueByName: function (name) {
                $('option[data-name="'+name+'"]', select).attr('selected', 'selected');
                select.trigger('change');
            },
            setValueById: function (id) {
                select.val(id);
                select.trigger('change');
            }
        });
    }

    return Component.create({
        tpl: tpl,
        cfg: null, // 初始化配置参数
        selects: [], // 缓存联动下拉框

        /*
        * cfg: Array
        * 初始化参数，其中包含每个联动下拉框的配置对象，该配置对象设置了下拉框的选择器，数据源URL，
        * 请求参数，服务器返回数据的解析设置，默认选项设置，空白选项设置
        *
        */
        init: function (cfg) {

            if(!Util.isArray(cfg)) {
                     console.error('cfg is not a array!');
            }

            var self = this;
            $.each(cfg, function (idx, item) {
                var select = $(item.selector);
                select.attr('data-level',idx);
                self.bindEvent(item.selector);
                extend(select);
                self.selects[idx] = select;
            });

            this.cfg = cfg;

            this.render(0);
        },
        render: function (sid) {

            var self      = this,
                selectCfg   = this.cfg[sid], // 获取第sid个<select>的配置对象
                select      = $(selectCfg.selector), // 取得select节点对象
                url         = selectCfg.url, // 数据请求路径
                data        = selectCfg.data || {}, // 发送给服务器的请求参数
                blankItem   = selectCfg.blankItem|| true, // 是否插入空白选项行
                selected    = selectCfg.selected || {}, // 根据数据id或者index设置默认选项
                jsonReader  = $.extend({},{  // 设置服务器返回数据的解析
                    ds: 'obj',  // 下拉框数据源在返回的服务器数据中的存储字段
                    id: 'id',   // 下拉框<option>标签中value属性值所在的服务器数据对象中的属性名
                    name: 'name' // 下拉框<option>标签的显示文本所在的服务器数据对象中的属性名
                },selectCfg.jsonReader);

            if(Util.isStrEmpty(url)) {
                console.error("The url is empty.");
            }

            /*
            * 获取该级下拉框之前的所有下拉框的选项，并根据每个下拉框的paramMap规则，将其选择项的信息添加到data中
            *
            */
            var prevs = this.cfg.slice(0,sid);
            $.each(prevs, function (idx,item) {
                var pre = $(item.selector);
                if(item.paramMap) {
                    item.paramMap.id && (data[item.paramMap.id] = pre.val());
                    item.paramMap.name && (data[item.paramMap.name] = $('option:selected',pre).text());
                }
            });

            var m = DModel.init();

            // 发送请求
            m.fetchRemote(url, {
                data: data,
                async: false,
                success: function (data) {
                    var ds = data[jsonReader.ds],
                        datas = [];

                    if(Util.isArray(ds)) {
                        // 如果返回的数据是数组，则遍历数组，根据设定的jsonReader规则，进行解析
                        $.each(ds, function (idx, item) {
                            datas.push({
                                id: item[jsonReader.id],
                                name: item[jsonReader.name]
                            });
                        })
                    } else if(Util.isObject(ds)){
                        // 如果返回的数据是对象，则将其属性和属性值分别取出，设置为数据对象的id、name属性值
                        for(var i in ds) {
                            datas.push({
                                id: i,
                                name: ds[i]
                            });
                        }
                    } else {
                        console.log('The '+jsonReader.ds+' in response data is not a obj or array.');
                    }

                    var html = juicer(self.tpl,{data: datas,blankItem: blankItem,selected: selected});
                    select.empty().append(html);

                    // 触发下拉框的change事件，同时也会使得所有下级下拉框发送数据请求
                    select.trigger('change');
                }
            })
        },
        bindEvent: function (selector) {

            var self = this;
            var selectCtr = new ViewController(selector,{
                                events: {'change': 'changeItem'
                                },
                                handlers: {
                                    changeItem: function (e) {
                                        var level = Util.parseInteger(selectCtr.el.attr('data-level')) + 1, // 下一级下拉框所在级数
                                            nextSelect = self.cfg[level] && $(self.cfg[level].selector); // 下一级下拉框节点对象

                                        var nexts = self.cfg.slice(level); // 取得所有下级下拉框的配置

                                        // 当下拉框值改变时，所有下级下拉框置空
                                        $.each(nexts, function (idx, item) {
                                           $(item.selector).empty();
                                        });

                                        // 如果存在下一级下拉框，则根据上级下拉框的选择请求数据，进行渲染
                                        if(nextSelect && nextSelect.length > 0) {
                                            var select = $(e.currentTarget),
                                                el = $('option:selected', select);
                                            self.render(level, {
                                                id: select.val(),
                                                name: el.text()
                                            });

                                        }
                                    }
                                }
                            });

        }

    });
})
