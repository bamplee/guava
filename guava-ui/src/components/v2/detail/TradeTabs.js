import React, {useEffect, useState} from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';
import {Range, Tabs} from 'antd-mobile';

import {areaTypeState, filterAreaState, regionState, tradeDateState, tradeTypeState} from '../../datatool/state';
import {getChart, getRegionChart} from '../../datatool/api';

import classNames from 'classnames/bind';
import styles from './tradeChart.module.scss';
import {Line} from 'react-chartjs-2';
import Chart from 'chart.js';
import moment from 'moment';
import {getEndArea, getStartArea} from '../../constant';
import Loading from './Loading';
import {Badge} from "antd-mobile/lib/index";
import VersusChart from "./VersusChart";
import VersusList from "./VersusList";
import {useHistory, useLocation, useParams} from "react-router-dom";

const cx = classNames.bind(styles);

const TradeTabs = () => {
    const {regionType, regionId, tabId} = useParams();
    // const location = useLocation();
    const history = useHistory();
    const [period, setPeriod] = useState([moment().subtract('years', 3), moment()]);
    // const [startDate, setStartDate] = useState(period[0]);
    // const [endDate, setEndDate] = useState(period[1]);
    // const [showGap, setShowGap] = useState(false);

    return (
        <Tabs tabs={[
            {title: '기간별', value: ''},
            // {title: '전체 기간', value: 'p'},
            {title: '매매/전세', value: 'g'},
            // {title: <Badge dot>비교</Badge>, value: 'c'}
            ]}
              initialPage={!tabId ? 0 : (tabId === 'g' ? 1 : 2)}
              onChange={(tab, index) => {
                  console.log('onChange', index, tab);
              }}
              onTabClick={(tab, index) => {
                  console.log(tab);
                  history.replace('/' + regionType + '/' + regionId + '/' + tab.value)
                  // if (index === 0) {
                  //     setShowGap(false);
                  //     setStartDate(moment().subtract('years', 3));
                  //     setEndDate(moment());
                  // }
                  // if (index === 1) {
                  //     setShowGap(false);
                  //     setStartDate(moment('20060101', 'YYYYMMDD'));
                  //     setEndDate(moment());
                  // }
                  // if (index === 2) {
                  //     history.replace('/' + regionType + '/' + regionId + '/g')
                  //     // setShowGap(true);
                  //     // setStartDate(moment().subtract('years', 3));
                  //     // setEndDate(moment());
                  // }
                  // if (index === 3) {
                  //     history.replace('/' + regionType + '/' + regionId + '/c')
                  // }
                  // console.log('onTabClick', index, tab);
              }}
        />
    );
};

export default TradeTabs;