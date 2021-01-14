import React, {useEffect, useRef, useState} from 'react'

import {useHistory, useLocation} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import {useRecoilState, useRecoilValue} from 'recoil';
import {buildingListState, isQueryModeState, queryListState, regionState} from '../datatool/state';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import {Button, WingBlank} from 'antd-mobile';
import {fetchSearch, fetchSearchBuilding} from '../datatool/api';
import {TABLE_OPTION} from '../constant';
import CloseOutlined from '@ant-design/icons/es/icons/CloseOutlined';

const cx = classNames.bind(styles);

const GuavaSearchHeader = () => {
    const location = useLocation();
    const history = useHistory();
    const queryInput = useRef(null);
    const [query, setQuery] = useState([]);
    const [isQueryMode, setIsQueryMode] = useRecoilState(isQueryModeState);
    const [queryList, setQueryList] = useRecoilState(queryListState);
    const [loading, setLoading] = useState(false);
    const [buildingList, setBuildingList] = useRecoilState(buildingListState);
    const region = useRecoilValue(regionState);

    useEffect(() => {
        if (queryInput && queryInput.current) {
            queryInput.current.focus();
        }
    }, [queryInput]);

    const onChangeQuery = (query) => {
        setIsQueryMode(true);
        setQuery(query.target.value);
        if (query.target.value.length === 0) {
            setQueryList([]);
        }
    };

    const fetchRegion = async () => {
        if (query !== '') {
            const pattern = /([^가-힣a-z\x20])/i;
            if (!pattern.test(query) && !loading) {
                setLoading(true);
                let regionList = await fetchSearch(query);
                let buildingList = await fetchSearchBuilding(query);
                setQueryList(regionList);
                setBuildingList(buildingList);
                setLoading(false);
            }
        }
    };

    return (
        <>
            <div className={cx('header_container')}>
                <div className={cx('title_container')}>
                    <WingBlank>
                        {/*<div className={cx('left')} onClick={() => fetchRegion()}>*/}
                        <div className={cx('left')}
                             onClick={() => history.push(location.pathname.replace('/search', ''))}>
                            {/*<SearchOutlined/>*/}
                            <ArrowLeftOutlined/>
                        </div>
                    </WingBlank>
                    <div className={cx('center')}>
                        {/*<span className={cx('title')}>{region.address}</span>*/}
                        <input ref={queryInput}
                               onKeyPress={(e) => {
                                   if (e.key === 'Enter') {
                                       fetchRegion();
                                   }
                               }}
                               onClick={() => setIsQueryMode(true)}
                               onChange={onChangeQuery}
                               className={cx('search_input')}
                               placeholder="지역, 아파트명을 입력하세요">
                        </input>
                    </div>
                    <WingBlank>
                        {/*<div className={cx('right')} onClick={() => history.push(location.pathname.replace('/search', ''))}>*/}
                        <div className={cx('right')}>
                            <CloseOutlined style={{visibility: 'hidden'}}/>
                        </div>
                    </WingBlank>
                </div>
                {/*<div className={cx('filter_container')}>*/}
                {/*    /!*<div className={cx('filter_select')}>*!/*/}
                {/*    /!*    <Button className={cx('filter_btn', tradeType === 'trade' ? '' : 'filter_btn_active')}*!/*/}
                {/*    /!*            type={'primary'}*!/*/}
                {/*    /!*            onClick={() => setTradeType(tradeType === 'trade' ? 'rent' : 'trade')}*!/*/}
                {/*    /!*            // onClick={() =>*!/*/}
                {/*    /!*            //     Modal.operation([*!/*/}
                {/*    /!*            //         {text: '매매', onPress: () => setTradeType('trade')},*!/*/}
                {/*    /!*            //         {text: '전/월세', onPress: () => setTradeType('rent')},*!/*/}
                {/*    /!*            //     ])*!/*/}
                {/*    /!*            // }*!/*/}
                {/*    /!*            size={'small'}>{tradeType === 'trade' ? '매매' : '전월세'}</Button>*!/*/}
                {/*    /!*</div>*!/*/}
                {/*    <div className={cx('filter_select')}>*/}
                {/*        <Button*/}
                {/*            className={cx('filter_btn', 'filter_btn_active')}*/}
                {/*            type={'primary'}*/}
                {/*            // onClick={() =>*/}
                {/*            //     Modal.operation([*/}
                {/*            //         {text: '매매', onPress: () => setTradeType('trade')},*/}
                {/*            //         {text: '전/월세', onPress: () => setTradeType('rent')},*/}
                {/*            //     ])*/}
                {/*            // }*/}
                {/*            size={'small'}>검색하기</Button>*/}
                {/*    </div>*/}
                {/*    <div className={cx('filter_select')}>*/}
                {/*        <Button className={cx('filter_btn', 'filter_btn_active')}*/}
                {/*                type={'primary'} size={'small'}*/}
                {/*            // onClick={() =>*/}
                {/*            //     Modal.operation([*/}
                {/*            //         {text: '실거래', onPress: () => setTableOption(TABLE_OPTION.TRADE)},*/}
                {/*            //         {text: '호가', onPress: () => setTableOption(TABLE_OPTION.MARKET)},*/}
                {/*            //     ])}*/}
                {/*        >선택하기</Button>*/}
                {/*    </div>*/}
                {/*</div>*/}
            </div>
        </>
    )
};

export default GuavaSearchHeader;