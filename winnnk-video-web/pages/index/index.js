const app = getApp()

Page({
  data: {
    totalPage: 1,
    page: 1,
    videoList: [],
    serverUrl: '',
    screenWidth: 350,
    searchContent: ''
  },

  onLoad: function(params) {
    var me = this;
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    //搜索的内容，即desc
    var searchContent = params.search;
    //是否使用搜索
    var isSaveRecord = params.isSaveRecord;
    if (isSaveRecord == null || isSaveRecord == '' || isSaveRecord == undefined) {
      isSaveRecord = 0;
    }
    me.setData({
      screenWidth: screenWidth,
      searchContent: searchContent
    });
    //获取当前分页数
    var page = me.data.page;
    me.getAllVideoList(page, isSaveRecord);
  },

  getAllVideoList: function(page, isSaveRecord) {
    var me = this;
    var serverUrl = app.serverUrl;
    var searchContent = me.data.searchContent;
    wx.showLoading({
      title: '加载中',
    })
    wx.request({
      url: serverUrl + '/video/showAll?page=' + page + '&isSaveRecord=' + isSaveRecord,
      method: "POST",
      data: {
        videoDesc: searchContent
      },
      success: function(res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        //判断当前页是否是第一页，如果是，设置videoList为空
        if (page == 1) {
          me.setData({
            videoList: []
          });
        }
        //新查询出的视频列表
        var newVideoList = res.data.data.rows;
        //之前的视频列表
        var videoList = me.data.videoList;
        me.setData({
          videoList: videoList.concat(newVideoList),
          page: page,
          totalPage: res.data.data.total,
          serverUrl: serverUrl
        });
      }
    })
    wx.request({
      url: '',
    })
  },

  onReachBottom: function() {
    var me = this;
    var currentPage = me.data.page;
    var totalPage = me.data.totalPage;
    //如果当前页面=总页数，则无需上拉刷新
    if (currentPage == totalPage) {
      wx.showToast({
        title: '已经没有视频啦',
        icon: "none"
      })
      return;
    }
    var page = currentPage + 1;
    me.getAllVideoList(page, 0);
  },

  onPullDownRefresh: function() {
    wx.showNavigationBarLoading();
    wx.showToast({
      title: '刷新中',
      icon: "none"
    })
    this.getAllVideoList(1, 0);
  },

  showVideoInfo: function(e) {
    var me = this;
    var videoList = me.data.videoList;
    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(videoList[arrindex]);
    wx.redirectTo({
      url: '../videoinfo/videoinfo?videoInfo=' + videoInfo,
    })
  }

})