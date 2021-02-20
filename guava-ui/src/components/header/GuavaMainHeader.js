import React, {useEffect, useState} from 'react'

import {useHistory, useLocation} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import SearchOutlined from '@ant-design/icons/es/icons/SearchOutlined';
import {WingBlank} from 'antd-mobile';
import NumberOutlined from '@ant-design/icons/es/icons/NumberOutlined';
import {useRecoilState} from 'recoil';
import {showVersusSearchState, versusRegionListState} from '../datatool/state';

const cx = classNames.bind(styles);

const GuavaMainHeader = () => {
    const location = useLocation();
    const history = useHistory();
    const [showVersusSearch, setShowVersusSearch] = useRecoilState(showVersusSearchState);
    const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);

    useEffect(() => {
        setShowVersusSearch(false);
        setVersusRegionList([]);
    }, []);

    // history.push('/search')

    return (
        location.pathname !== '/search' &&
        <div className={cx('w-full fixed z-99999')}>
            <div className={cx('flex justify-between h-12 p-3 bg-guava')}>
                <div onClick={() => history.push('/')}
                     className={cx('flex items-center flex-none text-center text-white transform hover:scale-110 motion-reduce:transform-none cursor-pointer')}>
                    <svg className="w-5 h-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                         stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
                    </svg>
                </div>
                <div onClick={() => history.push('/search')}
                     className={cx('flex items-center justify-start flex-grow ml-3 text-white text-base font-light')}>
                    <p>지역/아파트 검색하기</p>
                </div>
                <div onClick={() => history.push('/search')}
                     className={cx('flex items-center flex-none text-center text-white')}>
                    <svg className="w-5 h-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                         stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                    </svg>
                </div>
            </div>
        </div>
    )
};

export default GuavaMainHeader;