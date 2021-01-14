import React, {useEffect} from 'react'

import {useHistory, useLocation} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import {useRecoilState} from 'recoil';
import {currentRegionState,} from '../datatool/state';
import SearchOutlined from '@ant-design/icons/es/icons/SearchOutlined';
import MenuOutlined from '@ant-design/icons/es/icons/MenuOutlined';
import {WingBlank} from 'antd-mobile';
import AntDesignOutlined from '@ant-design/icons/es/icons/AntDesignOutlined';
import ReconciliationOutlined from '@ant-design/icons/es/icons/ReconciliationOutlined';

const cx = classNames.bind(styles);

const GuavaSearch = () => {
    const history = useHistory();
    const location = useLocation();
    // const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [currentRegion, setCurrentRegion] = useRecoilState(currentRegionState);

    // useEffect(() => {
    //     ReactGA.initialize('UA-129316162-1');
    // }, []);

    useEffect(() => {
        // setShowFilter(location.pathname === '/');
        // setAreaType({areaId: ''});
    }, [location.pathname]);

    // const onChangeQuery = (e) => {
    //     let query = e.target.value;
    //     setQuery(query);
    //     if (query.length === 0) {
    //         setRegionList([]);
    //     }
    // };

    // const handleInput = async () => {
    //     // let result = await fetchCurrentRegion(currentRegion.y, currentRegion.x);
    //     // setRegion(result);
    //     setShowMap(false);
    //     setShowRegionSelector(!showRegionSelector);
    // };

    return (
        <>
            {
                <div className={cx('header_container')}>
                    <div className={cx('title_container')}>
                        <WingBlank>
                            <div className={cx('left')} onClick={() => history.push('/intro')}>
                                {/*<AntDesignOutlined/>*/}
                                <ReconciliationOutlined/>
                            </div>
                        </WingBlank>
                        <div className={cx('logo')}>
                            <span className={cx('title')}>우리동네</span>
                            <span className={cx('sub')}>아파트</span>
                            {/*<div>*/}
                            {/*    <span className={cx('title')}>guava</span>*/}
                            {/*    <span className={cx('sub_title')}>map</span>*/}
                            {/*    /!*{*!/*/}
                            {/*    /!*    currentRegion &&*!/*/}
                            {/*    /!*    <span className={cx('title')}>{currentRegion.name}</span>*!/*/}
                            {/*    /!*}*!/*/}
                            {/*</div>*/}
                        </div>
                        <WingBlank>
                            <div className={cx('right')} onClick={() => history.push('/search')}>
                                <SearchOutlined/>
                            </div>
                        </WingBlank>
                    </div>
                </div>
            }
            <div className={cx('bottom')}/>
        </>
    )
};

export default GuavaSearch;