/*!
 * combo -v1.0.0 (2015/11/16)
 * 
 * @description:
 *
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */

(function ($, win) {
    var Util  = $.Util;
    var comboWrap = '<div class="m-combo-dropdown">' +
                        '<div class="m-input-box l-text-combobox j-text-wrapper f-h30">' +
                            '<div class="u-select-title f-w200">{{placehoder}}</div>' +
                            '<div class="l-trigger l-trigger-cancel j-select-clear f-dn">' +
                                '<div class="l-trigger-icon"></div>' +
                            '</div>' +
                            '<div class="u-dropdown-icon"></div>' +
                        '</div>' +
                        '<div class="m-combo-tab f-dn">' +
                            '<div class="m-select-warp">' +
                                '<div class="u-select-tab"></div>' +
                                '<div class="j-select-content"></div>' +
                            '</div>' +
                        '</div>' +
                    '</div>';
    var selectTab = '{@each tabs as tab}<a >{{tab}}</a>{@/each}';
    var selectPanel = '<div class="m-select-panel f-dn">' +
                        '</div>';
    var selectPanelContent = '{@each data as d}' +
                            '<dl class="fn-clear">' +
                            '<dt>{{d.group}}</dt> ' +
                            '<dd  >' +
                            '{@each d.items as item}' +
                            '<a title="{{item.name}}" class="j-select-option" data-id="{{item.id}}" data-abbr="{{item.abbr}}" data-full="{{item.full}}"  data-keyword="{{item.keyword}}"href="javascript:void(0);">{{item.name}}</a>' +
                            '{@/each}' +
                            ' </dd>' +
                            '</dl>' +
                            '{@/each} ';

    var defaults = {
      placehoder: '',
      width: 240,
      tabData:[],
      groups: ['A-G', 'H-K', 'L-S', 'T-Z'],
      categories: {},
      dataRoot: 'obj',
      filterFields: ['name','abbrPY','fullPY'],
      content: ''
    };

    function init (opts) {
        var self = this;
        this.opt = $.extend({},defaults, opts);
        this.tabs = [];
        $.each(this.opt.tabsData, function () {
            self.tabs.push(this.name);
        });

        this.$c  = $('<div class="m-combo-select-container">');
        this.tabPanelDatas = [];
        createWrapper.call(this);
        renderTabs.call(this);
        bindEvents.call(this);
        activeTab.call(this, 0);
    }

    function createWrapper() {
        var wrapHtml = juicer(comboWrap, {
            placehoder: this.opt.placehoder,
            tabs: []
        });
        var $wrap = $(wrapHtml);
        this.$c.append($wrap).appendTo(this.parent());
        this.attr('readonly',true).css({opacity: 0}).appendTo($('.m-input-box',this.$c));
        $('.j-select-clear',this.$c).css('z-index', 10);
    }
    function getGroup(c) {
        var p = this.opt;
        var groups = p.groups;
        var g = "";
        for(var i=0; i< groups.length; i++) {
            var arr = groups[i].split('-');
            if(arr[0].toUpperCase()<=c && c<= arr[1].toUpperCase()) {
                g = groups[i];
                break;
            }
        }
        return g;
    }
    function renderTabs() {
        var tabhHtml = juicer(selectTab,{ tabs: this.tabs});
        $('.u-select-tab', this.$c).append(tabhHtml);
        for(var i=0; i< this.tabs.length; i++) {
            $('.j-select-content', this.$c).append(selectPanel);
        }
    }
    function changeStatus(index) {
        if(index >= this.tabs.length) return ;
        $('.m-select-panel', this.$c).hide();
        $('.m-select-panel', this.$c).eq(index).show();
        $('.u-select-tab a', this.$c).removeClass('current');
        $('.u-select-tab a', this.$c).eq(index).addClass('current');
    }
    function activeTab(index) {
        changeStatus.call(this,index);
        renderTabPanel.call(this,index);
    }
    function renderTabPanel(index,value) {
        var p = this.opt;
        var ds = p.tabsData[index];
        var dm = $.DataModel.init();
        var self = this;
        value = value || [];

        function limitInput() {
            var val = this.val();
            if(ds.maxlength && val.length>ds.maxlength) {
                this.val(val.substr(0,ds.maxlength));
            }
        }
        if(!ds){
            close.call(this);
            return ;
        }
        if(!ds.url) {
            var $panel = $('.j-select-content .m-select-panel', self.$c).eq(index);
            var $textArea = $('<textarea class="j-select-input f-h50 f-ww f-pl5 f-pt5">').on('input keyup',function () {
                limitInput.call($textArea);
                updateTitle.call(self);
                self.val(getValue.call(self));
            }).on('blur', function () {
                limitInput.call($textArea);
            });
            if(ds.maxlength) {
                $textArea.attr('maxlength', ds.maxlength);
            }
            $textArea.val(value[index]||'');
            $panel.empty().append($textArea);
            updateTitle.call(self);
            return;
        }
        this.trigger("beforeAjaxSend",index);
        makeParams.call(this, index);
        dm.fetchRemote(ds.url,{data: ds.params, success: function (remoteData) {
            var data = remoteData[p.dataRoot];
            var panelData = self.tabPanelDatas[index] = handleData.call(self,data,ds);
            var renderData = [];
            $.each(self.opt.groups, function () {
                var items = [];
                for(var i=0; i<panelData.length; i++) {
                    var d = panelData[i];
                    if(Util.isStrEquals(d.group, this)) {
                        items.push(d);
                    }
                }
                renderData.push({
                    group: this,
                    items: items
                })
            });

            var html = juicer(selectPanelContent, {data: renderData});
            var $selectPanel = $('.j-select-content .m-select-panel', self.$c).eq(index);
            $selectPanel.empty().append(html);
            var $options = $('.j-select-option',$selectPanel);
            if(index>0 && $options.length==1 && !value.length) {
                var $parent = $('.j-select-content .m-select-panel', self.$c).eq(index-1).find('.j-select-option.current');
                var $current = $options.eq(0);
                var parentText = $parent.text();
                if(Util.isStrEquals(parentText, $current.text())) {
                    $current.click();
                }
            }
            if(index==2 && !$options.length) {
                renderTabPanel.call(self,index+1,value);
                self.val(getValue.call(self));
                return;
            }

            var $option = null;
            if(Util.hasChinese(value[index])){
                $option = $(Util.format('.j-select-option[title="{0}"]', value[index]), $selectPanel);
            } else {
                $option = $(Util.format('.j-select-option[data-id="{0}"]', value[index]), $selectPanel);
            }
            if($option.length) {
                resetTab.call(self, index);
                $option.addClass('current');
                renderTabPanel.call(self,index+1,value);
            } else {
                $selectPanel.nextAll().empty();
            }
            updateTitle.call(self);
            self.val(getValue.call(self));
        }});
    }
    function makeParams(index) {
        var p = this.opt;
        var ds = p.tabsData[index];
        if(index>0) {
            var $parentPanel = $('.m-select-panel', this.$c).eq(index-1);
            var $parnetOption = $('.j-select-option.current', $parentPanel);
            ds.params = $.extend({},ds.params,{pid: $parnetOption.attr('data-id')})
        }
    }
    function updateTitle() {
        var $options = $('.j-select-option.current', this.$c);
        var titles = [];
        $options.each(function (i) {
            var text = $(this).text();
            if(i==0||(i>0 && !Util.isStrEquals(titles[i-1], text) ) ) {
                titles.push(text);
            }
        });
        var textAreaText = $('.j-select-input', this.$c).length?$('.j-select-input', this.$c).val(): '';
        $('.u-select-title', this.$c).text(titles.join('') + textAreaText);
    }
    function close() {
        var $inputBox = $('.m-input-box',this.$c),
            $tabWrap = $('.m-combo-tab',this.$c),
            $dropdownIcon = $('.u-dropdown-icon', this.$c);
        $inputBox.removeClass('l-text-focus');
        $dropdownIcon.removeClass('f-combo-focus');
        $tabWrap.hide();
    }
    function open() {
        var $inputBox = $('.m-input-box',this.$c),
            $tabWrap = $('.m-combo-tab',this.$c),
            $dropdownIcon = $('.u-dropdown-icon', this.$c);
        $('.m-input-box').not($inputBox).removeClass('l-text-focus');
        $('.u-dropdown-icon').not($dropdownIcon).removeClass('f-combo-focus');
        $('.m-combo-tab').not($tabWrap).hide();
        $inputBox.addClass('l-text-focus');
        $dropdownIcon.addClass('f-combo-focus');
        $tabWrap.show();
    }
    function handleData(data,ds) {
        var self = this;
        var dataTemp = [];
        for(var key in data) {
            var id = data[key][ds.code];
            var name = data[key][ds.value];
            var abbrChars = $.Pinyin.getAbbrChars(name);
            var fullChars = $.Pinyin.getFullChars(name);
            dataTemp.push(Util.format("{0}||{1}||{2}||{3}",abbrChars,fullChars,id,name));
        }
        dataTemp.sort();

        var panelDatas = [];
        $.each(dataTemp, function () {
            var arr = this.split("||");
            var g = getGroup.call(self, arr[0].charAt(0));
            panelDatas.push({
                abbr: arr[0], full: arr[1], id: arr[2], name: arr[3], group: g, keyword: Util.format("{0} {1} {2}",arr[0],arr[1].toUpperCase(),arr[3].toUpperCase())
            });
        });
       return panelDatas;
    }

    function setValue(value) {
        value = value || [];
        renderTabPanel.call(this,0,value);
    }

    function getValue() {
        var $options = $('.j-select-option.current', this.$c);
        var $textArea = $('.j-select-input', this.$c);
        var values = [];
        var names = [];
        $.each($options, function () {
            values.push($(this).attr('data-id'));
            names.push($(this).attr('title'));
        });

        var $panel3 = $('.j-select-content .m-select-panel', this.$c).eq(2);
        if(!$('.j-select-option',$panel3).length) {
            values.push("");
            names.push("");
        }
        if($textArea.length) {
            values.push($textArea.val());
            names.push($textArea.val());
        }

        return JSON.stringify({keys:values, names: names});
    }
    function resetTab(idx) {
        var $selectContent = $('.j-select-content .m-select-panel', this.$c);
        if(idx) {
            var $panel = $selectContent.eq(idx);
            $('.j-select-option',$panel).removeClass('current');
        } else {
            $('.j-select-option',$selectContent).removeClass('current');
        }
    }
    function bindEvents() {
        var self = this;
        var $body = $(document.body),
            $inputBox = $('.m-input-box',this.$c),
            $tabWrap = $('.m-combo-tab',this.$c),
            $clearBtn = $('.j-select-clear',this.$c);

        $inputBox.click(function () {
            open.call(self);
        }).mouseover(function () {
            $clearBtn.show();
        }).mouseout(function () {
            $clearBtn.hide();
        });
        $clearBtn.click(function () {
            activeTab.call(self,0);
            self.focus();
        });
        $body.click(function (e) {
            if(!$(e.target).closest('.m-combo-dropdown').length){
                close.call(self);
            }
        });
        this.$c.on('click','.u-select-tab>a', function () {
            var idx= $(this).index();
            changeStatus.call(self,idx);
        }).on('click','.j-select-option',function () {
            var idx = $('.u-select-tab .current',$tabWrap).index();
            resetTab.call(self, idx);
            $(this).addClass('current');
            updateTitle.call(self);
            self.val(getValue.call(self));
            if(idx+1<= self.tabs.length) {
                activeTab.call(self, idx+1);
            }
            self.blur();
        });

        this.on("beforeAjaxSend", function (event, index) {
            var ds = self.opt.tabsData[index];
            Util.isFunction(ds.beforeAjaxSend)? ds.beforeAjaxSend.call(self,ds, $('.j-select-option.current', this.$c)): '';
        });
    }
    $.fn.addressDropdown = function (opts) {
        init.call(this, opts);
        this.setValue = setValue;
        $.data(this[0],'combo',this);
    }
})(jQuery, window);